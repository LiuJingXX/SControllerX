package com.decawave.argomanager.scontroller.fragment.subfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.activity.devices.AddDeviceActivity;
import com.decawave.argomanager.scontroller.activity.devices.NewDeviceListFragment;
import com.decawave.argomanager.scontroller.activity.devices.NewDeviceManageActivity;
import com.decawave.argomanager.scontroller.activity.scenario.ExerciseRoomActivity;
import com.decawave.argomanager.scontroller.adapter.HomeDeviceViewPagerAdapter;
import com.decawave.argomanager.scontroller.fragment.devicefragment.DeviceWebFragment;
import com.decawave.argomanager.scontroller.fragment.devicefragment.DeviceWebFragment1;
import com.decawave.argomanager.ui.MainActivity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private TextView sub_home_textview1;
    private ImageView ic_home_add_device;
    private List<Fragment> viewPageList;
    private ViewPager viewPager;



    public static HomeFragment newInstance(String s) {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_home, container, false);

        //切换家庭菜单框
        sub_home_textview1 = (TextView)view.findViewById(R.id.sub_home_textview1);
        //新增设备按钮
        ic_home_add_device=(ImageView)view.findViewById(R.id.ic_home_add_device);

        //获取viewPager
        viewPager = (ViewPager)view.findViewById(R.id.vp_home_device);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        //初始化viewPager//加载viewpage ,这次加载的有设备list的DeviceListFragment和设备web图的DeviceWebFragment
        //获取两个fragment
        NewDeviceListFragment fragment1 = new NewDeviceListFragment();
        DeviceWebFragment fragment2 = new DeviceWebFragment();
        DeviceWebFragment1 fragment3 = new DeviceWebFragment1();
        // 将要分页显示的View装入数组中
        viewPageList= new ArrayList<Fragment>();
        viewPageList.add(fragment1);
        viewPageList.add(fragment2);
        viewPageList.add(fragment3);
        //实例化适配器
        viewPager.setAdapter(new HomeDeviceViewPagerAdapter(getActivity().getSupportFragmentManager(), viewPageList));
//        viewPager.addOnPageChangeListener(this);//设置页面切换时的监听器(可选，用了之后要重写它的回调方法处理页面切换时候的事务)

        //设置点击切换家庭，弹出选择家庭菜单框
        sub_home_textview1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(getActivity(), v);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.change_home, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.exit:
                                Toast.makeText(getActivity(), "切换为实验室设备",Toast.LENGTH_LONG).show();
                                break;
                            case R.id.set:
                                Toast.makeText(getActivity(), "切换为401设备",Toast.LENGTH_LONG).show();
                                break;
                            case R.id.account:
                                Toast.makeText(getActivity(), "管理家庭",Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                //显示(这一行代码不要忘记了)
                popup.show();
            }
        });

        //为房间添加新设备
        ic_home_add_device.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(getActivity(), v);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.add_device, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_add:
                                Intent intent0 = new Intent(getContext(), AddDeviceActivity.class);
                                startActivity(intent0);
                                break;
                            case R.id.item_location:
                                Intent intent1 = new Intent(getContext(), NewDeviceManageActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.item_map:
                                Intent intent2 = new Intent(getContext(), MainActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.item_exercise_room:
                                Intent intent3 = new Intent(getContext(), ExerciseRoomActivity.class);
                                startActivity(intent3);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                //显示(这一行代码不要忘记了)
                popup.show();
            }
        });

    }
}
