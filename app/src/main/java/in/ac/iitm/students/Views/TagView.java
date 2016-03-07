package in.ac.iitm.students.Views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.ac.iitm.students.ProfileEditActivity;
import in.ac.iitm.students.R;


/**
 * Created by aqel on 23/4/15.
 */
public class TagView extends LinearLayout {
    Boolean isSelected;
    String tag;
    TextView tvTag;
    View view;
    ImageView ivState;
    LinearLayout layout;

    public TagView(Context context) {
        super(context);
        view = inflate(getContext(), R.layout.tag, null);
        addView(view);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ZoberTag, 0, 0);
        view = LayoutInflater.from(context).inflate(R.layout.tag, this);

        try {
            // get the text and colors specified using the names in attrs.xml
            tag = a.getString(R.styleable.ZoberTag_tag);
            isSelected = a.getBoolean(R.styleable.ZoberTag_selected, false);
        } finally {
            a.recycle();
        }
        setTag(isSelected, tag);

    }

    public void setTag(Boolean isSelectedf, String tag) {
        this.tag = tag;
        this.isSelected = isSelectedf;


        ;
        tvTag = (TextView) this.findViewById(R.id.tvTag);
        ivState = (ImageView) this.findViewById(R.id.ivState);
        layout = (LinearLayout) this.findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelected = !isSelected;
                if (isSelected) {
                    tvTag.setTextColor(Color.WHITE);
                    ivState.setImageResource(R.drawable.ic_tick);
                    setBackground(R.drawable.tag_oval_bg_pressed);
                } else {
                    tvTag.setTextColor(getResources().getColor(R.color.colorAccent));
                    ivState.setImageResource(R.drawable.ic_plus);
                    setBackground(R.drawable.tag_oval_bg);
                }
                Log.d("ajskdf","tag view");
                //  Intent startActivity = new Intent(ProfileActivity.this,ProfileEditActivity.class);
                // startActivity(startActivity);
            }
        });
        tvTag.setText(tag);
        if (isSelected) {
            tvTag.setTextColor(Color.WHITE);
            ivState.setImageResource(R.drawable.ic_tick);
            setBackground(R.drawable.tag_oval_bg_pressed);
        } else {
            tvTag.setTextColor(getResources().getColor(R.color.colorAccent));
            ivState.setImageResource(R.drawable.ic_plus);
            setBackground(R.drawable.tag_oval_bg);
        }
    }


    public void SetTextSize(int size) {
        tvTag = (TextView) this.findViewById(R.id.tvTag);
        tvTag.setTextSize(size);
    }

    public Boolean getIsSelected(){
        return isSelected;
    }

    public void SetLayoutHeight(int size) {

        layout = (LinearLayout) this.findViewById(R.id.layout);
        layout.getLayoutParams().height = size;
    }

    private void setBackground(int drawable) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(getResources().getDrawable(drawable));
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackground(getResources().getDrawable(drawable));
            }
        }
    }
}
