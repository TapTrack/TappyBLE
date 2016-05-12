/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessageVista;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandadapter.CommandAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail.CommandDetailView;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandfamilyadapter.CommandFamilyAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DefaultPrettySheetAdapter;
import com.taptrack.tcmptappy.ui.mvp.BackHandler;
import com.taptrack.tcmptappy.ui.mvp.TransientStatePersistable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

/**
 * Pretty in name only, not code...
 */
@CoordinatorLayout.DefaultBehavior(PrettyCommandSheetView.Behavior.class)
public class PrettyCommandSheetView extends RelativeLayout implements SendTcmpMessageVista, TransientStatePersistable, BackHandler {

    @BindView(R.id.rv_command_family_selector)
    RecyclerView commandFamilySelector;
    @BindView(R.id.rv_specific_command_selector)
    RecyclerView commandSelector;
    @BindView(R.id.v_two_level_shadow)
    View shadowView;
    @BindView(R.id.fab_repeat_message)
    FloatingActionButton repeatMessageButton;
    @BindView(R.id.rfl_reveal_frame)
    RevealFrameLayout revealFrameLayout;
    @BindView(R.id.cdv_command_detail)
    CommandDetailView commandDetailView;
    //this squasher is used to get around a strange bug in the design library
    @BindView(R.id.vg_fab_squasher)
    ViewGroup fabSquasher;

    private final TimeInterpolator interpolator= new FastOutLinearInInterpolator();

    private boolean isShowingCommands = false;
    private boolean isExpanded = false;

    private final CommandFamilyAdapter.CommandFamilySelectedListener familySelectedListener
            = new CommandFamilyAdapter.CommandFamilySelectedListener() {
        @Override
        public void onCommandFamilySelected(int identifier) {
            setSelectedFamily(identifier,true);
        }
    };

    private final CommandAdapter.CommandSelectedListener commandSelectedListener =
            new CommandAdapter.CommandSelectedListener() {
        @Override
        public void onCommandSelected(int commandType, int x, int y) {
            setSelectedCommand(commandType, x, y);
        }
    };

    private final CommandDetailView.CommandSendListener commandSendListener =
            new CommandDetailView.CommandSendListener() {
        @Override
        public void onValidSendRequest(TCMPMessage message) {
            if(presenter != null)
                presenter.sendTcmpMessage(message);

            unselectFamily(true);
            hideCommandDetailViewIfVisible(true);
        }

        @Override
        public void onCancel() {
            selectedCommand = PrettySheetAdapter.NONE;
            hideCommandDetailViewIfVisible(true);
        }
    };

    private final OnClickListener repeatClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(presenter != null)
                presenter.repeatLastMessage();
        }
    };

    private final OnLayoutChangeListener layoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//            if(isInDetailMode)
//                updateShimHeight();
//            showCommandDetailViewIfHidden(false,0,0);
        }
    };

    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if(isInDetailMode &&
                    ((detailExpandAnimation == null || !detailExpandAnimation.isRunning()
                    && (detailCircularFloofAnimation == null || !detailCircularFloofAnimation.isRunning()))))
                showCommandDetailViewIfHidden(false,0,0);
        }
    };

    private CommandFamilyAdapter familyAdapter;
    private CommandAdapter commandAdapter;
    private ValueAnimator currentAnimation;
    private ValueAnimator detailExpandAnimation;
    private SupportAnimator detailCircularFloofAnimation;

    private static final float ANIM_EXPAND_VELOCITY_DIP = 2f;
    private static final float ANIM_CONTRACT_VELOCITY_DIP = 2f;
    private final int DEF_GRID_COLUMN_COUNT = 3;

    private float animExpandVelocityPx;
    private float animContractVelocityPx;

    private static final float ANIM_BUTTON_VELOCITY_DIP = 0.2f;
    private float animAppearVelocityPx;
    private ValueAnimator buttonAnimator;

    private boolean hasInitialized;

    private PrettySheetAdapter prettySheetAdapter;

    private int selectedFamily = PrettySheetAdapter.NONE;
    private int selectedCommand = PrettySheetAdapter.NONE;
    private boolean isInDetailMode = false;
    private SendTcmpMessagePresenter presenter;

    public static final String KEY_DETAIL_STATEBUNDLE = "DETAIL_STATE";
    public static final String KEY_SELECTED_FAMILY = "SELECTED_FAMILY";
    public static final String KEY_SELECTED_COMMAND = "SELECTED_COMMAND";
    public static final String KEY_DETAIL_MODE = "IS_DETAIL_MODE";
    private int detailShowX;
    private int detailShowY;

    public PrettyCommandSheetView(Context context) {
        super(context);
        init(context,null,0,0);
    }

    public PrettyCommandSheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public PrettyCommandSheetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PrettyCommandSheetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if(!hasInitialized) {
            LayoutInflater.from(context).inflate(R.layout.view_two_level_navigator, this);

            ButterKnife.bind(this);

            commandFamilySelector.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            familyAdapter = new CommandFamilyAdapter(familySelectedListener);
            familyAdapter.setHasStableIds(true);
            commandFamilySelector.setAdapter(familyAdapter);

            commandSelector.setLayoutManager(new GridLayoutManager(context, getGridColumnCount()));
            commandAdapter = new CommandAdapter(commandSelectedListener);
            commandAdapter.setHasStableIds(true);
            commandSelector.setAdapter(commandAdapter);

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            animContractVelocityPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ANIM_CONTRACT_VELOCITY_DIP, metrics);
            animExpandVelocityPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ANIM_EXPAND_VELOCITY_DIP, metrics);
            animAppearVelocityPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ANIM_BUTTON_VELOCITY_DIP, metrics);

            fabSquasher.setVisibility(GONE);
//            repeatMessageButton.setVisibility(GONE);
//            repeatMessageButton.getLayoutParams().height = 0;
//            repeatMessageButton.getLayoutParams().width = 0;
//            repeatMessageButton.requestLayout();
            repeatMessageButton.setOnClickListener(repeatClickListener);

            commandDetailView.addOnLayoutChangeListener(layoutChangeListener);
            commandDetailView.setSendListener(commandSendListener);

            getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

            setPrettySheetAdapter(new DefaultPrettySheetAdapter());
            loadRecyclerAdapters();
            hasInitialized = true;
        }
    }


    private void setSelectedCommand(int commandType, int x, int y) {
        this.selectedCommand = commandType;
        updateCommandDetailView();
        showCommandDetailViewIfHidden(true, x, y);
    }

    private void updateCommandDetailView() {
        if(prettySheetAdapter != null) {
            commandDetailView.setDetailAdapter(prettySheetAdapter.getDetailAdapterForItem(selectedCommand),true);
        }
    }

    private void setSelectedFamily(int family, boolean animate) {
        selectedFamily = family;
        loadRecyclerAdapters();
        showCommandSelectorIfHidden(animate);
    }

    public void setPrettySheetAdapter(PrettySheetAdapter adapter) {
        this.prettySheetAdapter = adapter;
    }

    private void loadRecyclerAdapters() {
        if(prettySheetAdapter != null) {
            familyAdapter.setSelectedId(selectedFamily);
            familyAdapter.setItems(prettySheetAdapter.getCommandFamilyOptions());
            familyAdapter.notifyDataSetChanged();

            commandAdapter.setItems(prettySheetAdapter.getCommandsForFamily(selectedFamily));
            commandAdapter.notifyDataSetChanged();
        }
    }

    private void unselectFamily(boolean animate) {
        selectedFamily = PrettySheetAdapter.NONE;
        loadRecyclerAdapters();
        hideCommandSelector(animate);
    }

    private void showCommandSelectorIfHidden(boolean animate) {
        expandCommandSelectorToFitFamily(animate);
        isShowingCommands = true;
    }

    private void hideCommandSelector(boolean animate) {
        if(isShowingCommands)
            collapseCommandSelector(animate);
        isShowingCommands = false;
    }

    public void enableRepeat(boolean animated) {
        if(animated) {
            if(buttonAnimator != null)
                buttonAnimator.cancel();
            buttonAnimator = createBaseRepeatButtonAnimator(false,animAppearVelocityPx,fabSquasher);
            buttonAnimator.start();
        }
        else {
            fabSquasher.setVisibility(VISIBLE);
            fabSquasher.getLayoutParams().height = LayoutParams.WRAP_CONTENT;;
            fabSquasher.getLayoutParams().width = LayoutParams.WRAP_CONTENT;;
            fabSquasher.requestLayout();
        }

    }

    public void disableRepeat(boolean animated) {
        if(animated) {
            if(buttonAnimator != null)
                buttonAnimator.cancel();
            buttonAnimator = createBaseRepeatButtonAnimator(true,animAppearVelocityPx,fabSquasher);
            buttonAnimator.start();
        }
        else {
            fabSquasher.setVisibility(GONE);
            fabSquasher.getLayoutParams().height = 0;
            fabSquasher.getLayoutParams().width = 0;
            fabSquasher.requestLayout();
        }

    }

    private ValueAnimator createBaseRepeatButtonAnimator(boolean isForHide, float pxVelocity, final View button) {
        final int currentHeight = button.getMeasuredHeight();
        final int currentWidth = button.getMeasuredWidth();
        final int desiredHeight;
        final int desiredWidth;
        final int finalVisibility;
        final ViewGroup.LayoutParams finalLayoutParams = button.getLayoutParams();
        if(isForHide) {
            desiredHeight = 0;
            desiredWidth = 0;
            finalVisibility = GONE;
            finalLayoutParams.height = 0;
            finalLayoutParams.width = 0;
        }
        else {
            finalLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            finalLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            button.measure(
                    MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(getContext().getResources().getDimensionPixelSize(R.dimen.familySelectorTopRowHeight),MeasureSpec.AT_MOST));
            desiredHeight = button.getMeasuredHeight();
            desiredWidth = button.getMeasuredWidth();
            finalVisibility = VISIBLE;
            button.setVisibility(VISIBLE);
            if(currentHeight < 1 || currentWidth < 1) {
                button.getLayoutParams().width = 1;
                button.getLayoutParams().height = 1;
            }
        }

        int differenceWidth = Math.abs(desiredWidth-currentWidth);
        long duration = (long) (differenceWidth / pxVelocity);
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1.0f);

        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float procession = (float) animation.getAnimatedValue();
                button.getLayoutParams().height = currentHeight + (int) (procession * (desiredHeight - currentHeight));
                button.getLayoutParams().width = currentWidth + (int) (procession * (desiredWidth - currentWidth));
                button.requestLayout();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                button.setVisibility(finalVisibility);
                button.setLayoutParams(finalLayoutParams);
                button.requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return valueAnimator;
    }

    private void showCommandDetailViewIfHidden(boolean animate, int x, int y) {
        if(detailExpandAnimation != null && detailExpandAnimation.isStarted())
            detailExpandAnimation.cancel();

        if(detailCircularFloofAnimation != null && detailCircularFloofAnimation.isRunning())
            detailCircularFloofAnimation.cancel();

        this.detailShowX = x;
        this.detailShowY = y;

        commandDetailView.setShimHeight(0);
        commandDetailView.getLayoutParams().height = 0;
        commandDetailView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        commandDetailView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int newHeight = commandDetailView.getMeasuredHeight();

        int minimumHeight = (getContext().getResources().getDimensionPixelSize(R.dimen.familySelectorTopRowHeight)) +
                (getContext().getResources().getDimensionPixelSize(R.dimen.commandDetailGridExpandableMaxHeight));

        int targetHeight = Math.max(newHeight,minimumHeight);
        int shimHeight = targetHeight - newHeight;

        if(shimHeight > 0)
            commandDetailView.setShimHeight(shimHeight);

        int currentHeight = commandDetailView.getLayoutParams().height;
        if(animate) {
            commandDetailView.getLayoutParams().height = minimumHeight;
//            interpolatedExpandCommandDetail(x,y,minimumHeight, targetHeight, animExpandVelocityPx);
            interpolatedExpandCommandDetail(x,y,minimumHeight, targetHeight, 1);
        }
        else {
            isExpanded = true;
            commandDetailView.getLayoutParams().height = targetHeight;
            commandDetailView.setVisibility(VISIBLE);
            commandDetailView.invalidate();
        }
        isInDetailMode = true;
    }

    private void hideCommandDetailViewIfVisible(boolean animate) {
        if(detailExpandAnimation != null && detailExpandAnimation.isStarted())
            detailExpandAnimation.cancel();

        if(detailCircularFloofAnimation != null && detailCircularFloofAnimation.isRunning())
            detailCircularFloofAnimation.cancel();

        int targetHeight = 0;
        int currentHeight = commandDetailView.getLayoutParams().height;
        if(animate) {
            interpolatedContractCommandDetail(this.detailShowX, this.detailShowY, currentHeight, targetHeight, animContractVelocityPx);
        } else {
            commandDetailView.getLayoutParams().height = 0;
            commandDetailView.setVisibility(GONE);
            commandDetailView.invalidate();
        }
        isInDetailMode = false;
    }

    private void interpolatedExpandCommandDetail(final int x,final int y, int current, int desired, float velocity) {
        ValueAnimator valueAnimator = createBaseCommandDetailAnimator(current, desired, velocity);

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isExpanded = false;
                commandDetailView.setVisibility(VISIBLE);
                initiateCommandEntryFloof(x,y);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isExpanded = true;
//                commandDetailView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isExpanded = true;

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        if(commandDetailView.getLayoutParams().height < 1)
            commandDetailView.getLayoutParams().height = 1;
        commandDetailView.setVisibility(VISIBLE);

        valueAnimator.start();

        detailExpandAnimation = valueAnimator;
    }

    private void initiateCommandEntryFloof(final int x, final int y) { //, int finalTop, int finalLeft, int finalBottom, int finalRight){
        // get the center for the clipping circle
        Point centrePoint = new Point(x,y);
        commandDetailView.getX();

        int[] detailLoc = new int[2];
        commandDetailView.getLocationOnScreen(detailLoc);
        int width = commandDetailView.getMeasuredWidth();
        int height = commandDetailView.getMeasuredHeight();
        
        Rect rect = new Rect();
        commandDetailView.getGlobalVisibleRect(rect);
        Point corner1 = new Point(detailLoc[0],detailLoc[1]);
        Point corner2 = new Point(detailLoc[0],detailLoc[1] + height);
        Point corner3 = new Point(detailLoc[0] + width,detailLoc[1]);
        Point corner4 = new Point(detailLoc[0] + width,detailLoc[1] + height);
        int finalRadius = getMaxDistance(centrePoint,corner1,corner2,corner3,corner4);

        int rX = x - detailLoc[0];
        int rY = y - detailLoc[1];

        detailCircularFloofAnimation =
                ViewAnimationUtils.createCircularReveal(commandDetailView, rX, rY, 0, finalRadius);
        detailCircularFloofAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        detailCircularFloofAnimation.setDuration(750);
        detailCircularFloofAnimation.start();
    }

    private int getMaxDistance(Point centre, Point... points) {
        long largest = 0;
        for(int i = 0; i < points.length; i++) {
            long tmp = (centre.x*centre.x) + (centre.y*centre.y);
            largest = tmp >  largest ? tmp : largest;
        }
        return (int) Math.ceil(Math.sqrt(largest));
    }

    private void interpolatedContractCommandDetail(final int x, final int y, int current, int desired, float velocity) {
        if(detailExpandAnimation != null && detailExpandAnimation.isStarted())
            detailExpandAnimation.cancel();

        if(detailCircularFloofAnimation != null && detailCircularFloofAnimation.isRunning())
            detailCircularFloofAnimation.end();

        ValueAnimator valueAnimator = createBaseCommandDetailAnimator(current, desired, velocity);

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                initiateCommandHideFloof(x,y);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                commandDetailView.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
        detailExpandAnimation = valueAnimator;
    }

    private void initiateCommandHideFloof(int x, int y) {

    }

    private ValueAnimator createBaseCommandDetailAnimator(int current, int desired, float velocity) {
        int difference = Math.abs(desired-current);
        long duration = (long) (difference / velocity);
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(current, desired);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                commandDetailView.getLayoutParams().height = (int) animation.getAnimatedValue();
                commandDetailView.requestLayout();
            }
        });
        return valueAnimator;
    }


    private int getGridColumnCount() {
        return DEF_GRID_COLUMN_COUNT;
    }

    public void expandCommandSelectorToFitFamily(boolean animate) {
        int rowCount = (int) Math.ceil((float) commandAdapter.getItemCount() / getGridColumnCount());
        int currentHeight = commandSelector.getLayoutParams().height;

        int targetHeight = rowCount * (getContext().getResources().getDimensionPixelSize(R.dimen.commandDetailGridHeight));
        int maxHeight = (getContext().getResources().getDimensionPixelSize(R.dimen.commandDetailGridExpandableMaxHeight));
        if(targetHeight > maxHeight)
            targetHeight = maxHeight;
        if(animate) {
            interpolatedExpandCommandSheet(currentHeight, targetHeight, animExpandVelocityPx);
        }
        else {
            commandSelector.getLayoutParams().height = targetHeight;
            commandSelector.setVisibility(VISIBLE);
            commandSelector.requestLayout();
        }
    }

    public void collapseCommandSelector(boolean animate) {
        final int currentHeight = commandSelector.getLayoutParams().height;
        final int targetHeight = 0;
        if(animate) {
            interpolatedContractCommandSheet(currentHeight, targetHeight, animContractVelocityPx);
        }
        else {
            commandSelector.getLayoutParams().height = 0;
            commandSelector.setVisibility(GONE);
            commandSelector.requestLayout();
        }
    }


    private void interpolatedExpandCommandSheet(int current, int desired, float velocity) {
        if(currentAnimation != null && currentAnimation.isStarted())
            currentAnimation.end();


        ValueAnimator valueAnimator = createBaseCommandSheetAnimator(current, desired, velocity);

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                commandSelector.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        if(commandSelector.getLayoutParams().height < 1)
            commandSelector.getLayoutParams().height = 1;
        commandSelector.setVisibility(VISIBLE);

        valueAnimator.start();
        currentAnimation = valueAnimator;
    }

    private void interpolatedContractCommandSheet(int current, int desired, float velocity) {
        if(currentAnimation != null && currentAnimation.isStarted())
            currentAnimation.end();

        ValueAnimator valueAnimator = createBaseCommandSheetAnimator(current, desired, velocity);

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                commandSelector.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
        currentAnimation = valueAnimator;
    }

    public int getVisualHeightMinusShadow() {
        return this.getMeasuredHeight() - shadowView.getMeasuredHeight();
    }

    private ValueAnimator createBaseCommandSheetAnimator(int current, int desired, float velocity) {
        int difference = Math.abs(desired-current);
        long duration = (long) (difference / velocity);
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(current, desired);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                commandSelector.getLayoutParams().height = (int) animation.getAnimatedValue();
                commandSelector.requestLayout();
            }
        });
        return valueAnimator;
    }

    public boolean isInDetailMode() {
        return isInDetailMode;
    }

    @Override
    public void registerPresenter(SendTcmpMessagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void unregisterPresenter() {
        this.presenter = null;
    }

    @Override
    public void storeTransientState(Bundle bundle) {
        Bundle detailBundle = new Bundle();
        commandDetailView.storeTransientState(detailBundle);
        bundle.putBundle(KEY_DETAIL_STATEBUNDLE, detailBundle);

        //disabling persistence of the command selection because
        // of measurement issues with orientation changes
        bundle.putInt(KEY_SELECTED_COMMAND, selectedCommand);
        bundle.putBoolean(KEY_DETAIL_MODE, isInDetailMode);

//        bundle.putInt(KEY_SELECTED_COMMAND, PrettySheetAdapter.NONE);
        bundle.putInt(KEY_SELECTED_FAMILY, selectedFamily);
//        bundle.putBoolean(KEY_DETAIL_MODE, false);

    }

    @Override
    public void restoreTransientState(Bundle bundle) {
        selectedFamily = bundle.getInt(KEY_SELECTED_FAMILY,PrettySheetAdapter.NONE);
        selectedCommand = bundle.getInt(KEY_SELECTED_COMMAND,PrettySheetAdapter.NONE);
        loadRecyclerAdapters();
        setSelectedFamily(selectedFamily,false);

        if(selectedCommand != PrettySheetAdapter.NONE) {
            isInDetailMode = bundle.getBoolean(KEY_DETAIL_MODE, false);
            updateCommandDetailView();
            //must do this one last as it depends on previous operations
            if (bundle.containsKey(KEY_DETAIL_STATEBUNDLE)) {
                commandDetailView.restoreTransientState(bundle.getBundle(KEY_DETAIL_STATEBUNDLE));
            }

            if(isInDetailMode) {
                showCommandDetailViewIfHidden(false,0,0);
            }
            else {
                hideCommandDetailViewIfVisible(false);
            }
        }



    }

    @Override
    public boolean onBackPressed() {
        if(isInDetailMode) {
            isInDetailMode = false;
            hideCommandDetailViewIfVisible(true);
            return true;
        }
        else if (selectedFamily != PrettySheetAdapter.NONE) {
            unselectFamily(true);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    public static class Behavior extends CoordinatorLayout.Behavior<PrettyCommandSheetView> {
        public Behavior() {
            super();
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, PrettyCommandSheetView child, MotionEvent ev) {
            Rect rect = new Rect();
            child.getGlobalVisibleRect(rect);
            if(!rect.contains((int)ev.getRawX(),(int)ev.getRawY())) {
                if(!child.isInDetailMode())
                    child.unselectFamily(true);
            }
            return super.onInterceptTouchEvent(parent, child, ev);
        }
    }
}
