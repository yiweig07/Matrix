package com.laioffer.matrix;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReportDialog extends Dialog {
    private int cx;
    private int cy;
    private RecyclerView mRecyclerView;
    private ReportRecyclerViewAdapter mRecyclerViewAdapter;
    private ViewSwitcher mViewSwitcher;
    private String mEventType;
    //Event specs
    private ImageView mImageCamera;
    private Button mBackButton;
    private Button mSendButton;
    private EditText mCommentEditText;
    private ImageView mEventTypeImg;
    private TextView mTypeTextView;


    public ReportDialog(@NonNull Context context) {
        this(context, R.style.MyAlertDialogStyle);
    }

    public ReportDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static ReportDialog newInstance(Context context, int cx, int cy) {

        ReportDialog dialog = new ReportDialog(context, R.style.MyAlertDialogStyle);
        dialog.cx = cx;
        dialog.cy = cy;
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View dialogView = View.inflate(getContext(), R.layout.dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //set up animation
        setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                animateDialog(dialogView, true);
            }
        });

        setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_BACK) {
                    animateDialog(dialogView, false);
                    return true;
                }
                return false;
            }
        });
        setupRecyclerView(dialogView);

        mViewSwitcher = dialogView.findViewById(R.id.viewSwitcher);
        //set view switch animation
        Animation slide_in_left = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        Animation slide_out_right = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
        mViewSwitcher.setInAnimation(slide_in_left);
        mViewSwitcher.setOutAnimation(slide_out_right);

        setUpEventSpecs(dialogView);
    }

    private void animateDialog(View dialogView, boolean open) {
        final View view = dialogView.findViewById(R.id.dialog);
        int w = view.getWidth();
        int h = view.getHeight();
        int endRadius = (int) Math.hypot(w, h);

        if (open) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(500);
            revealAnimator.start();
        } else {
            Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                    dismiss();
                }
            });
            animator.setDuration(500);
            animator.start();
        }
    }

    private void setupRecyclerView(View dialogView) {
        mRecyclerView = dialogView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerViewAdapter = new ReportRecyclerViewAdapter(getContext(), Config.listItems);
        mRecyclerViewAdapter.setClickListener(new ReportRecyclerViewAdapter.OnClickListener() {
            @Override
            public void setItem(String item) {
                //for switch item
                showNextViewSwitcher(item);
            }
        });

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void showNextViewSwitcher(String item) {
        mEventType = item;
        if (mEventType != null) {
            mViewSwitcher.showNext();
            mTypeTextView.setText(mEventType);
            mEventTypeImg.setImageDrawable(ContextCompat.getDrawable(getContext(), Config.trafficMap.get(mEventType)));
        }
    }

    private void setUpEventSpecs(final View dialogView) {
        mImageCamera = dialogView.findViewById(R.id.event_camera_img);
        mBackButton = dialogView.findViewById(R.id.event_back_button);
        mSendButton = dialogView.findViewById(R.id.event_send_button);
        mCommentEditText = dialogView.findViewById(R.id.event_comment);
        mEventTypeImg = dialogView.findViewById(R.id.event_type_img);
        mTypeTextView = dialogView.findViewById(R.id.event_type);

    }
}
