package com.scyh.applock.task;

import java.util.ArrayList;
import java.util.List;

import com.scyh.applock.R;
import com.scyh.applock.model.LockStage;
import com.scyh.applock.ui.view.LockPatternView;
import com.scyh.applock.utils.LockPatternUtils;

import android.content.Context;


public class GestureCreatePresenter implements GestureCreateContract.Presenter {
    private GestureCreateContract.View mView;
    private Context mContext;

    public GestureCreatePresenter(GestureCreateContract.View view, Context context) {
        mView = view;
        mContext = context;
    }

    @Override
    public void updateStage(LockStage stage) {
        mView.updateUiStage(stage); //更新UiStage
        if (stage == LockStage.ChoiceTooShort) { //如果少于4个点
            mView.updateLockTip(mContext.getResources().getString(stage.headerMessage, LockPatternUtils.MIN_LOCK_PATTERN_SIZE), true);
        } else {
            if (stage.headerMessage == R.string.lock_need_to_unlock_wrong) {
                mView.updateLockTip(mContext.getResources().getString(R.string.lock_need_to_unlock_wrong), true);
                mView.setHeaderMessage(R.string.lock_recording_intro_header);
            } else {
                mView.setHeaderMessage(stage.headerMessage); //
            }
        }
        // same for whether the patten is enabled
        mView.lockPatternViewConfiguration(stage.patternEnabled, LockPatternView.DisplayMode.Correct);

        switch (stage) {
            case Introduction:  //介绍
                mView.Introduction(); //第一步
                break;
            case HelpScreen: //帮助（错误多少次后可以启动帮助动画）
                mView.HelpScreen();
                break;
            case ChoiceTooShort: //锁屏路径太短
                mView.ChoiceTooShort();
                break;
            case FirstChoiceValid: //第一步提交成功
                updateStage( LockStage.NeedToConfirm); //转跳到第二步
                mView.moveToStatusTwo();
                break;
            case NeedToConfirm:
                mView.clearPattern();  //第二步
                break;
            case ConfirmWrong:
                //第二步跟第一步不一样
                mView.ConfirmWrong();
                break;
            case ChoiceConfirmed:
                //第三步
                mView.ChoiceConfirmed();
                break;
        }
    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern, List<LockPatternView.Cell> mChosenPattern, LockStage mUiStage) {
        if (mUiStage ==  LockStage.NeedToConfirm) { //如果下一步
            if (mChosenPattern == null)
                throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
            if (mChosenPattern.equals(pattern)) {
                updateStage( LockStage.ChoiceConfirmed);
            } else {
                updateStage( LockStage.ConfirmWrong);
            }
        } else if (mUiStage ==  LockStage.ConfirmWrong) {
            if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
                updateStage( LockStage.ChoiceTooShort);
            } else {
                if (mChosenPattern.equals(pattern)) {
                    updateStage( LockStage.ChoiceConfirmed);
                } else {
                    updateStage( LockStage.ConfirmWrong);
                }
            }
        } else if (mUiStage ==  LockStage.Introduction || mUiStage ==  LockStage.ChoiceTooShort) {
            if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
                updateStage( LockStage.ChoiceTooShort);
            } else {
                mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
                mView.updateChosenPattern(mChosenPattern);
                updateStage( LockStage.FirstChoiceValid);
            }
        } else {
            throw new IllegalStateException("Unexpected stage " + mUiStage + " when " + "entering the pattern.");
        }
    }

    @Override
    public void onDestroy() {

    }
}
