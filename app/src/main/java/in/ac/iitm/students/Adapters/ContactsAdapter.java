package in.ac.iitm.students.Adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.ac.iitm.students.Fragments.ImportantContacts;
import in.ac.iitm.students.Objects.Contacts;
import in.ac.iitm.students.Objects.GridImage;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Utils;

/**
 * Created by arunp on 28-Jan-16.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{
    String ISEXPANDED = "isExpanded0";
    Context context;
    ArrayList<Contacts> Contacts=new ArrayList<Contacts>();

    public ContactsAdapter(Context context ,ArrayList<Contacts> Contacts ) {
        this.context=context;
        this.Contacts =Contacts;
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, final int position) {
        Contacts contact =Contacts.get(position);
        holder.imageButton
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(Contacts.get(position).getPhoneNumber());
            }
        });
        holder.DisplayName.setText(contact.getName());
        holder.number.setText(contact.getPhoneNumber());
        addAction(holder.layout,holder.Expanded,150);
    }

    @Override
    public int getItemCount() {
        return Contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout Expanded;
        TextView number,DisplayName;
        AppCompatImageButton imageButton;
        CardView layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (CardView) itemView.findViewById(R.id.layout);
            Expanded = (RelativeLayout) itemView.findViewById(R.id.hidencontainer);
            number = (TextView) itemView.findViewById(R.id.number);
            DisplayName = (TextView) itemView.findViewById(R.id.dispName);
            imageButton = (AppCompatImageButton) itemView.findViewById(R.id.call);
        }
    }
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
    private void expand(View summary, int value) {
        //set Visible
        //int finalHeight = summary.getHeight();
        ValueAnimator mAnimator;
        summary.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, 300);
        mAnimator = slideAnimator(0, value, summary);
        mAnimator.start();
    }

    public void addAction(final View layout, final View summary, final int value) {

        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (summary.getVisibility() == View.GONE) {
                    if (Utils.getprefBool(ISEXPANDED, context)) {
                        collapse(ImportantContacts.ExpandedLayout);
                    } else {
                        Utils.saveprefBool(ISEXPANDED, true, context);
                    }
                    ImportantContacts.ExpandedLayout = summary;
                    expand(summary, value);
                } else {
                    if (Utils.getprefBool(ISEXPANDED, context)) {
                        Utils.saveprefBool(ISEXPANDED, false, context);
                    }
                    collapse(summary);
                }
            }
        });
    }


    private void collapse(final View summary) {
        int finalHeight = summary.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, summary);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                summary.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final View summary) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


}
