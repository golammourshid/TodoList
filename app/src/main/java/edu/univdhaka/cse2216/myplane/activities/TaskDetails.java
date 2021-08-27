package edu.univdhaka.cse2216.myplane.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.univdhaka.cse2216.myplane.R;

import edu.univdhaka.cse2216.myplane.domain.Tasks;


public class TaskDetails extends Fragment {

    TextView heading,type,date,time,details;
    String id;
    Tasks tasks;
    ImageView imageView;
    View view;
    Bundle bundle;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_task_details, container, false);
        bundle  = getArguments();
        bindWidget();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Task Details");
    }

    public void bindWidget()
    {
        heading = view.findViewById(R.id.heading);
        type = view.findViewById(R.id.tasktype);
        date = view.findViewById(R.id.taskdate);
        time = view.findViewById(R.id.tasktime);
        details = view.findViewById(R.id.taskdetails);
        imageView = view.findViewById(R.id.imageID);
        heading.setText(String.valueOf(bundle.getString("taskName")));
        type.setText(String.valueOf(bundle.getString("taskType")));
        date.setText(String.valueOf(bundle.getString("taskDate")));
        time.setText(String.valueOf(bundle.getString("taskTime")));

        details.setText(String.valueOf(bundle.getString("taskDetails")));
        setImage(String.valueOf(bundle.getString("taskType")));
    }
    private void setImage(String type)
    {
        if (type.equalsIgnoreCase("Education"))  imageView.setImageResource(R.drawable.study);
        else if (type.equalsIgnoreCase("Family")) imageView.setImageResource(R.drawable.family);
        else if (type.equalsIgnoreCase("Fun")) imageView.setImageResource(R.drawable.fun);
        else if (type.equalsIgnoreCase("Sports")) imageView.setImageResource(R.drawable.sports);
        else if (type.equalsIgnoreCase("Works")) imageView.setImageResource(R.drawable.office);
    }
}