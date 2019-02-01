package kr.co.ibks.platformteam.android.kkotpet.view.walkthrough;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kr.co.ibks.platformteam.android.kkotpet.R;
import kr.co.ibks.platformteam.android.kkotpet.util.CommonUtil;

/**
 * Created by choi on 2016. 11. 7..
 */

public class WalkThroughPagerAdapter extends PagerAdapter {
    private Context context;

    public WalkThroughPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }


    //ViewPager가 현재 보여질 Item(View객체)를 생성할 필요가 있는 때 자동으로 호출
    //쉽게 말해, 스크롤을 통해 현재 보여져야 하는 View를 만들어냄.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : ViewPager가 보여줄 View의 위치(가장 처음부터 0,1,2,3...)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub

        View view = CommonUtil.getViewInflater(context, R.layout.item_walkthrough, null);

        ImageView image = (ImageView) view.findViewById(R.id.img_Walkthrough);

        switch (position) {
            case 0:
                image.setImageResource(R.drawable.walk_through_01);
                break;
            case 1:
                image.setImageResource(R.drawable.walk_through_02);
                break;
            case 2:
                image.setImageResource(R.drawable.walk_through_03);
                break;
        }

        container.addView(view);

        return view;
    }

    //화면에 보이지 않은 View는파쾨를 해서 메모리를 관리함.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : 파괴될 View의 인덱스(가장 처음부터 0,1,2,3...)
    //세번째 파라미터 : 파괴될 객체(더 이상 보이지 않은 View 객체)
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시
        container.removeView((View) object);
    }

    //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
    @Override
    public boolean isViewFromObject(View v, Object obj) {
        // TODO Auto-generated method stub
        return v == obj;
    }
}

