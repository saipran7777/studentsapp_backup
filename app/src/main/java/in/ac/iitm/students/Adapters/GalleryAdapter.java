package in.ac.iitm.students.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import in.ac.iitm.students.Objects.GridImage;
import in.ac.iitm.students.R;

import java.util.ArrayList;

/**
 * Created by arunp on 06-Nov-15.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
    Context context;
    private DisplayImageOptions options;
    ArrayList<GridImage> Images=new ArrayList<GridImage>();
    public GalleryAdapter(Context context,ArrayList<GridImage> Images) {
        this.context=context;
        this.Images=Images;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.demo)
                .showImageOnFail(R.drawable.demo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(4)
                .build();

    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.gallery_grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
        holder.description.setText(Images.get(position).getDescription());
        String ImgUrl =Images.get(position).getImg();
        Log.d("Log error", "error");

        ImageLoader.getInstance()
                .displayImage(ImgUrl, holder.imgThumbnail, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                       /* holder.progressBar.setProgress(0);
                        holder.progressBar.setVisibility(View.VISIBLE);*/
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                       /* holder.progressBar.setVisibility(View.GONE);*/
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        holder.progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                        holder.progressBar.setProgress(Math.round(100.0f * current / total));
                    }
                });


    }

    @Override
    public int getItemCount() {
        return Images.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgThumbnail;
        public TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            description = (TextView)itemView.findViewById(R.id.description);
        }
    }
}
