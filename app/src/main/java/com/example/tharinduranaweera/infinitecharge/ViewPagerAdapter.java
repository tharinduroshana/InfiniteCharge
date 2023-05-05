package com.example.tharinduranaweera.infinitecharge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
* ViewPagerAdapter class which inherits from PageAdapter class.
* This lets the user to navigate through the sliding ViewPager we've integrated in the Home Activity.
* Images are used to keep the good standards and looking. (HCI Fact)
* */

public class ViewPagerAdapter extends PagerAdapter {

    //Declaration of variables
    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.charge, R.drawable.statistics};
    private String[] names = {"Charge and monitor", "Statistics"};
    private Class[] activities = {ChargingActivity.class, StatisticsActivity.class};

    //Constructor which takes a Context as a parameter
    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        imageView.setImageResource(images[position]);
        textView.setText(names[position]);

        //Adding onClickListener to the relevant image in the position at the moment
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0){
                    if(HomeActivity.isConnected){
                        ReadData.run = true;
                        Intent intent = new Intent(context, activities[position]);
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context, "Please connect the device first", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Intent intent = new Intent(context, activities[position]);
                    context.startActivity(intent);
                }
            }
        });

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);

    }
}
