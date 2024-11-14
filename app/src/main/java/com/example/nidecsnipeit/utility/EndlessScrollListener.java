package com.example.nidecsnipeit.utility;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private boolean isSearching = false;
    private int visibleThreshold = 5;
    private int totalItemCount, lastVisibleItem;



    public void setSearching(boolean searching) {
        isSearching = searching;
    }

    public EndlessScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isSearching() {
        return isSearching;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        totalItemCount = layoutManager.getItemCount();
        lastVisibleItem = layoutManager.findLastVisibleItemPosition();

        if (isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && !isSearching) {
            isLoading = false;
            loadMoreItems(totalItemCount, 10);
        }
    }
    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
    public abstract void loadMoreItems(int offset, int limit);
}
