package edu.univdhaka.cse2216.myplane.activities;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.univdhaka.cse2216.myplane.R;
import edu.univdhaka.cse2216.myplane.domain.Tasks;
import edu.univdhaka.cse2216.myplane.utils.DoneDatabase;

import java.util.ArrayList;
import java.util.List;


public class DoneList extends Fragment {


    private ListView listView;
    private CAdapter cAdapter;
    private DoneDatabase database;
    private ArrayList<Tasks> tasksList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_done_list, container, false);
        bindWidget(view);
        updateList();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Done Tasks");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void bindWidget(View view)
    {
        database = new DoneDatabase(getContext());
        listView = view.findViewById(R.id.list_done);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateList()
    {
        tasksList = new ArrayList<>();
        Cursor cursor = database.getAllData();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String taskname = cursor.getString(1);
                String isAlarm = cursor.getString(5);
                String id = cursor.getString(0);
                String taskDate = cursor.getString(2);
                String taskTime = cursor.getString(3);
                String taskType = cursor.getString(4);
                if (isAlarm == null) {
                    tasksList.add(new Tasks(R.drawable.alarm_off,id, taskname,taskDate,taskTime,taskType,0));
                } else if (isAlarm.equalsIgnoreCase("yes")) {
                    tasksList.add(new Tasks(R.drawable.alarm_on,id, taskname,taskDate,taskTime,taskType,1));
                } else {
                    tasksList.add(new Tasks(R.drawable.alarm_off, id, taskname,taskDate,taskTime,taskType,0));
                }
            }
        }
        cAdapter = new CAdapter(getContext(),tasksList,listView);
        listView.setAdapter(cAdapter);
    }

    private class CAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater inflater;
        ArrayList<Tasks> tasksList;
        List<Tasks> modelList;
        CAdapter(Context context, List<Tasks> list,ListView listView) {
            super();
            mContext = context;
            this.modelList = list;
            inflater = LayoutInflater.from(mContext);
            this.tasksList = new ArrayList<>();
            tasksList.addAll(modelList);
        }

        @Override
        public int getCount() {
            return modelList.size();
        }

        @Override
        public Object getItem(int position) {
            return modelList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.done_row,parent,false);


            final Tasks currentTask = (Tasks) getItem(position);
            TextView name = (TextView) listItem.findViewById(R.id.task_title2);
            name.setText(currentTask.getTask_name());
            ImageButton deleteButton = (ImageButton)listItem.findViewById(R.id.task_delete2);
            TextView taskDate = listItem.findViewById(R.id.DateText2);
            taskDate.setText(currentTask.getTask_date());
            TextView taskTime = listItem.findViewById(R.id.TimeText2);
            taskTime.setText(currentTask.getTask_time());
            LinearLayout linearLayout = (LinearLayout) listItem.findViewById(R.id.layout2);
            final TextView idText = listItem.findViewById(R.id.idText2);
            idText.setText(currentTask.getTask_id());

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(),"clciked",Toast.LENGTH_SHORT).show();
                    String details = database.getTaskDetails(currentTask.getTask_id());
                    Tasks newTask = currentTask;
                    newTask.setTask_details(details);
                    TaskDetails fragment=new TaskDetails();
                    fragment.setArguments(wrapInfo(newTask));
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.parentId,fragment,"doneToDetails");
                    transaction.addToBackStack("doneToDetails");
                    transaction.commit();
                    }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.delete_task_confirmation_title)
                            .setMessage(R.string.delete_task_confirmation_message)
                            .setIcon(R.drawable.question)
                            .setPositiveButton(R.string.delete,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteTask(idText.getText().toString(),position,currentTask);
                                        }
                                    })
                            .setNegativeButton(R.string.cancel, null)
                            .create()
                            .show();
                }
            });
            return listItem;
        }

        private void deleteTask(String rowId,int position,Tasks ctask)
        {
            int value = database.deleteData(rowId);
            modelList.remove(ctask);
            tasksList.remove(ctask);
            notifyDataSetChanged();
        }
    }
    private Bundle wrapInfo (Tasks task)
    {
        Bundle bundle=new Bundle();
        bundle.putString("taskName",task.getTask_name());
        bundle.putString("taskDate",task.getTask_date());
        bundle.putString("taskTime",task.getTask_time());
        bundle.putString("taskType",task.getTask_type());
        bundle.putString("taskDetails",task.getTask_details());
        return bundle;
    }

}