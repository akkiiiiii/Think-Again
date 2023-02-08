package com.nightowl.ThinkAgain.Fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nightowl.ThinkAgain.Classes.DialogManager;
import com.nightowl.ThinkAgain.Classes.DoNotOpenAppService;
import com.nightowl.ThinkAgain.Classes.NotificationManage;
import com.nightowl.ThinkAgain.Classes.PreChecker;
import com.nightowl.ThinkAgain.Database.DoNotOpenDatabase;
import com.nightowl.ThinkAgain.OverlayScreens.DoNotUseApp;
import com.nightowl.ThinkAgain.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class Select_Apps extends Fragment {


    private CardView constraintLayout;
    private ConstraintLayout totalApp;

    private AppCompatButton stop_start_service_btn;
    private CardView recycleviewParent;
    private RecyclerView ShowAppView;
    ArrayList<String> package_name = new ArrayList<>();
    ArrayList<Drawable> app_icon = new ArrayList<>();
    ArrayList<String> app_name = new ArrayList<>();
    private TextView totalCount;
    private TextView lockedCount;
    private DoNotOpenDatabase database;
    private DialogManager dialogManager;

    private PreChecker preChecker;

    public Select_Apps() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select__apps, container, false);


        constraintLayout = (CardView) view.findViewById(R.id.constraintLayout);
        totalApp = (ConstraintLayout) view.findViewById(R.id.total_app);
        recycleviewParent = (CardView) view.findViewById(R.id.recycleview_parent);
        ShowAppView = (RecyclerView) view.findViewById(R.id.ShowAppView);
        totalCount = (TextView) view.findViewById(R.id.total_count);
        lockedCount = (TextView) view.findViewById(R.id.locked_count);
        stop_start_service_btn = view.findViewById(R.id.stop_start_service_btn);



        dialogManager = new DialogManager(requireActivity(),requireContext());

        database = new DoNotOpenDatabase(requireContext());

        preChecker = new PreChecker(requireContext());

        CheckAppCountStartService();


        stop_start_service_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (database.get_package_count() <= 0)
                {
                    Toast.makeText(requireContext(), "First Lock Application. To Start", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    OnOffService();
                }


            }
        });

        ShowAppView.setHasFixedSize(true);

        final PackageManager pm = requireContext().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null && !pm.getLaunchIntentForPackage(packageInfo.packageName).equals(""))
            {
                package_name.add(packageInfo.packageName);
                app_name.add(String.valueOf(pm.getApplicationLabel(packageInfo)));
                try {
                    app_icon.add(requireContext().getPackageManager().getApplicationIcon(packageInfo.packageName));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }

        lockedCount.setText("Locked Applications = "+String.valueOf(database.get_package_count()));

        totalCount.setText("Total Applications = "+package_name.size());

        ShowAppView.setAdapter(new RecycleView_Adapter());



        return view;
    }



    private void OnOffService(){
        Intent intent = new Intent(requireActivity(), DoNotOpenAppService.class);

        if (preChecker.isAccessGranted() && preChecker.CanOverlay()){
            if (!isMyServiceRunning(DoNotOpenAppService.class)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    stop_start_service_btn.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.background));
                    stop_start_service_btn.setText("On");
                    Vibrate();
                    requireContext().startForegroundService(intent);
                }
            }else if (isMyServiceRunning(DoNotOpenAppService.class))
            {
                requireContext().stopService(intent);
                stop_start_service_btn.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.red_circular_round_bg));
                stop_start_service_btn.setText("Off");
                Vibrate();
            }
        }else{
            DialogManager dialogManager = new DialogManager(requireActivity(),requireContext());
            dialogManager.Show_Special_Access_Dialog();
            Toast.makeText(requireContext(), "Please provide permission in order to use this app.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        CheckAppCountStartService();
    }

    private void CheckAppCountStartService(){
        Intent intent = new Intent(requireActivity(), DoNotOpenAppService.class);


        if (preChecker.isAccessGranted() &&  preChecker.CanOverlay()){

            if (database.get_package_count() > 0){
                if (!isMyServiceRunning(DoNotOpenAppService.class)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        stop_start_service_btn.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.background));
                        stop_start_service_btn.setText("On");
                        requireContext().startForegroundService(intent);
                    }
                }
            }else{
                requireContext().stopService(intent);
                stop_start_service_btn.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.red_circular_round_bg));
                stop_start_service_btn.setText("Off");
            }

        }else{

            requireContext().stopService(intent);
            stop_start_service_btn.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.red_circular_round_bg));
            stop_start_service_btn.setText("Off");
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void StartService(){
        if (!isMyServiceRunning(DoNotOpenAppService.class)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stop_start_service_btn.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.background));
                stop_start_service_btn.setText("On");
                requireContext().startForegroundService(new Intent(requireActivity(),DoNotOpenAppService.class));
            }
        }
    }

    private void StopService(){
        if (database.get_package_count() == 1){
            if (isMyServiceRunning(DoNotOpenAppService.class)){
                requireContext().stopService(new Intent(requireActivity(),DoNotOpenAppService.class));
                stop_start_service_btn.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.red_circular_round_bg));
                stop_start_service_btn.setText("Off");
            }
        }

    }

    class RecycleView_Adapter extends RecyclerView.Adapter<RecycleView_Adapter.MyViewHolder>{


        @NonNull
        @Override
        public RecycleView_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_app_view,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleView_Adapter.MyViewHolder holder, int position) {


            if (holder.getAdapterPosition() == package_name.size()- 1){
                holder.view.setVisibility(View.INVISIBLE);
            }else{
                holder.view.setVisibility(View.VISIBLE);
            }

            if (database.app_exist(package_name.get(holder.getAdapterPosition()))){
                holder.lockImage.setVisibility(View.VISIBLE);
                holder.UnlockImage.setVisibility(View.INVISIBLE);

            }else{
                holder.lockImage.setVisibility(View.INVISIBLE);
                holder.UnlockImage.setVisibility(View.VISIBLE);
            }

            holder.lockImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckAppCountStartService();
                    StopService();
                    holder.lockImage.setVisibility(View.INVISIBLE);
                    holder.UnlockImage.setVisibility(View.VISIBLE);
                    database.delete_package(package_name.get(holder.getAdapterPosition()));
                    lockedCount.setText("Locked Applications = "+String.valueOf(database.get_package_count()));
                    Toast.makeText(requireActivity(), app_name.get(holder.getAdapterPosition()) + " " + "is removed from think again list", Toast.LENGTH_SHORT).show();
                    Vibrate();
                }
            });

            holder.UnlockImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (preChecker.isAccessGranted() && preChecker.CanOverlay()){

                           dialogManager.ShowLockDialogForSelectApp(package_name.get(holder.getAdapterPosition()),holder.lockImage,holder.UnlockImage,app_name.get(holder.getAdapterPosition()),lockedCount,stop_start_service_btn);


                        }else{
                            dialogManager.Show_Special_Access_Dialog();
                            Toast.makeText(requireContext(), "You need to give permissions in order to use this feature. Thank you.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(requireContext(), "You are using device lower can api 23. So you cannot use this feature.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            holder.packageName.setText(package_name.get(holder.getAdapterPosition()));
            holder.appName.setText(app_name.get(holder.getAdapterPosition()));
            holder.appIcon.setImageDrawable(app_icon.get(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return package_name.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView appIcon;
            private ConstraintLayout textContainer;
            private TextView appName;
            private TextView packageName;
            private View view;
            private ImageView lockImage;
            private ImageView UnlockImage;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                lockImage = (ImageView) itemView.findViewById(R.id.lock_image);
                UnlockImage = (ImageView) itemView.findViewById(R.id.unlock_image);
                appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
                textContainer = (ConstraintLayout) itemView.findViewById(R.id.text_container);
                appName = (TextView) itemView.findViewById(R.id.app_name);
                packageName = (TextView) itemView.findViewById(R.id.package_name);
                view = (View) itemView.findViewById(R.id.view);

            }
        }

    }

    private void Vibrate(){
        Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }
}