package com.fsdev.imeds;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Frankline Sable on 21/10/2017.
 */

public class Adapter_Menu extends RecyclerView.Adapter<Adapter_Menu.ViewHolder> {

    private List<Model_Menu> menuLists;
    private Context mContext;
    private TypeFace_Handler tp;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title,description,newCount;
        private RelativeLayout menuContainer;
        ImageButton icon;

        public ViewHolder(View v) {
            super(v);
            title=(TextView) v.findViewById(R.id.menuTitle);
            description=(TextView) v.findViewById(R.id.menuDescription);
            newCount=(TextView) v.findViewById(R.id.menuCount);
            icon=(ImageButton) v.findViewById(R.id.menuIcon);
            menuContainer=(RelativeLayout)v.findViewById(R.id.menuContainer);
        }
    }

    public Adapter_Menu(List<Model_Menu> menuLists, Context mContext) {
        this.menuLists = menuLists;
        this.mContext = mContext;
        tp=new TypeFace_Handler(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menuView=LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_card,parent,false);
        return new ViewHolder(menuView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Model_Menu menus=menuLists.get(position);
        //holder.title.setTypeface(tp.setFont("Helvetica Neue Light (Open Type).ttf"));
       // holder.description.setTypeface(tp.setFont("Roboto Light (Open Type).ttf"));
        holder.title.setText(menus.getMenuTitle());
        holder.description.setText(menus.getMenuDesc());

        holder.icon.setImageResource(menus.getMenuIcon());
        holder.menuContainer.setBackgroundResource(menus.getMenuBackground());

        if(menus.getMenuCount()>0){
            holder.newCount.setText(String.valueOf(menus.getMenuCount()));
            holder.newCount.setVisibility(View.VISIBLE);

        }else{
            holder.newCount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return menuLists.size();
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
