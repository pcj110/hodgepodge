package com.lsqidsd.hodgepodge.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lsqidsd.hodgepodge.R;
import com.lsqidsd.hodgepodge.ViewHolder.LoadMoreHolder;
import com.lsqidsd.hodgepodge.bean.DailyVideos;
import com.lsqidsd.hodgepodge.databinding.Loadbinding;
import com.lsqidsd.hodgepodge.databinding.VideosItemBinding;
import com.lsqidsd.hodgepodge.diyview.videoview.Jzvd;
import com.lsqidsd.hodgepodge.diyview.videoview.JzvdStd;
import com.lsqidsd.hodgepodge.viewmodel.HttpModel;
import com.lsqidsd.hodgepodge.viewmodel.VideosViewModule;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;


public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DailyVideos.IssueListBean.ItemListBean> listBeans;
    private Context context;
    private LayoutInflater layoutInflater;
    private RefreshLayout refreshLayout;
    private final int LOAD_MORE = -1;
    private final int NORMAL = 1;
    private String url;
    private JzvdStd jzvdStd;

    public VideoListAdapter(Context context, RefreshLayout refreshLayout) {

        this.context = context;

        this.refreshLayout = refreshLayout;
        this.layoutInflater = LayoutInflater.from(context);
    }
    public void addVideos(List<DailyVideos.IssueListBean.ItemListBean> listBeans,String url){
        this.listBeans = listBeans;
        this.url = url;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        VideosItemBinding videosItemBinding;
        Loadbinding loadbinding;

        switch (viewType) {
            case NORMAL:
                videosItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.videos_item, parent, false);
                viewHolder = new DataViewHolder(videosItemBinding);
                break;
            case LOAD_MORE:
                loadbinding = DataBindingUtil.inflate(layoutInflater, R.layout.loadmore, parent, false);
                viewHolder = new LoadMoreHolder(loadbinding);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreHolder) {
            HttpModel.getDailyVideos(listBeans, (a, b) -> {
                url = b;
                listBeans = a;
            }, refreshLayout, url, "");
        } else if (holder instanceof DataViewHolder) {
            ((DataViewHolder) holder).initData(listBeans.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return LOAD_MORE;
        } else {
            return NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return listBeans.size() + 1;
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        VideosItemBinding binding;

        public DataViewHolder(VideosItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void initData(DailyVideos.IssueListBean.ItemListBean bean) {
            jzvdStd = binding.video;
            jzvdStd.setUp(bean.getData().getPlayUrl(), bean.getData().getTitle(), Jzvd.SCREEN_WINDOW_LIST);
            Glide.with(context).load(bean.getData().getCover().getFeed()).into(jzvdStd.thumbImageView);
            binding.setVideoitem(new VideosViewModule(bean, context));
        }
    }

}
