package edu.univdhaka.cse2216.myplane.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.univdhaka.cse2216.myplane.R;
import edu.univdhaka.cse2216.myplane.domain.Tasks;
import edu.univdhaka.cse2216.myplane.utils.DataBase;
import edu.univdhaka.cse2216.myplane.utils.DoneDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private ListView listView;
    private CAdapter cAdapter;
    private FloatingActionButton addButton;
    private ArrayList<Tasks> tasksList;
    private DataBase dataBase;
    private DoneDatabase doneData;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_fragment, container, false);
        bindWidgets(view);
        updateList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Home");
    }

    //@TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void bindWidgets(View view)
    {
        dataBase = new DataBase(getContext());
        doneData = new DoneDatabase(getContext());
        addButton = view.findViewById(R.id.addItem);
        listView =  view.findViewById(R.id.list_todo);
        TextView emptyView=view.findViewById(R.id.emptyId);
        listView.setEmptyView(emptyView);
        addButton.setOnClickListener(this);
        ViewGroup footer = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.footer,listView,false);
        listView.addFooterView(footer);

        android.widget.SearchView searchView2= view.findViewById(R.id.searchView1);
        searchView2.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                cAdapter.filter(newText);
                return false;
            }
        });

        return;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.addItem) {
            Fragment fragment=new AddNew();
            FragmentManager fragmentManager= getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("ActivityType","newAdd");
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.parentId,fragment);
            fragmentTransaction.addToBackStack(String.valueOf(fragment));
            fragmentTransaction.commit();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateList()
    {
        tasksList = new ArrayList<>();
        Cursor cursor = dataBase.getAllData();
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
        cAdapter = new CAdapter(getActivity(),tasksList,listView);
        listView.setAdapter(cAdapter);
    }




    private class CAdapter extends BaseAdapter {

        Context mContext;
        LayoutInflater inflater;
        ArrayList<Tasks> tasksList;
        List<Tasks> modelList;

        public CAdapter(Context context, List<Tasks> list,ListView listView) {
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

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void filter(String charText){
            charText=charText.toLowerCase(Locale.getDefault());
            modelList.clear();
            if(charText.length()==0) {
                modelList.addAll(tasksList);
            }
            else {
                for (Tasks model : tasksList){
                    if(model.getTask_name().toLowerCase(Locale.getDefault()).contains(charText)){
                        modelList.add(model);
                    }
                }
            }

            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View listItem = convertView;

            if(listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.task_row, parent, false);
            }

            final Tasks currentTask = (Tasks) getItem(position);

            final CheckBox checkBox =  listItem.findViewById(R.id.task_finish);
            ImageButton editButton = listItem.findViewById(R.id.task_edit);
            ImageButton deleteButton = listItem.findViewById(R.id.task_delete);
            ImageButton alarm = listItem.findViewById(R.id.alarmButton);
            alarm.setImageResource(currentTask.getIsAlarm());
            TextView name =  listItem.findViewById(R.id.task_title);
            name.setText(currentTask.getTask_name());
            TextView taskDate = listItem.findViewById(R.id.DateText);
            taskDate.setText(currentTask.getTask_date());
            TextView taskTime = listItem.findViewById(R.id.TimeText);
            taskTime.setText(currentTask.getTask_time());
            LinearLayout clicableLayout =  listItem.findViewById(R.id.clicableLayout);

            final TextView idText = listItem.findViewById(R.id.idText);
            idText.setText(currentTask.getTask_id());

            clicableLayout.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    String details = dataBase.getTaskDetails(currentTask.getTask_id());
                    Tasks newTask = currentTask;
                    newTask.setTask_details(details);
                    TaskDetails newfragment=new TaskDetails();
                    newfragment.setArguments(wrapInfo(newTask));
                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.parentId,newfragment,"homeToDetails");
                    transaction.addToBackStack("homeToDetails");
                    transaction.commit();
                    //mContext.startActivity(new Intent(mContext,TaskdetailsActivity.class).putExtra("task", newTask));
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

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String details = dataBase.getTaskDetails(currentTask.getTask_id());
                    Tasks newTask = currentTask;
                    newTask.setTask_details(details);
                    AddNew newfragment=new AddNew();
                    Bundle bundle = wrapInfo(currentTask);
                    bundle.putString("ActivityType","edit");
                    newfragment.setArguments(bundle);
                    android.app.FragmentTransaction transaction=getFragmentManager().beginTransaction();
                    transaction.replace(R.id.parentId,newfragment);
                    transaction.addToBackStack(String.valueOf(newfragment));
                    transaction.commit();
                    //mContext.startActivity(new Intent(mContext,AddNewTask.class).putExtra("task", newTask));
                }
            });


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(mContext,"Task Done!", LENGTH_LONG).show();
                    doneData.insertData(currentTask);
                    deleteTask(idText.getText().toString(),position,currentTask);
                    checkBox.toggle();
                }
            });



            return listItem;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void deleteTask(String rowId, int position, Tasks ctask)
        {
            int value = dataBase.deleteData(rowId);
            modelList.remove(ctask);
            tasksList.remove(ctask);
            ((MainActivity)getActivity()).cancelAlarm(Integer.parseInt(ctask.getTask_id()));
            notifyDataSetChanged();
        }
        private Bundle wrapInfo (Tasks task)
        {
            Bundle bundle=new Bundle();
            bundle.putString("taskName",task.getTask_name());
            bundle.putString("taskDate",task.getTask_date());
            bundle.putString("taskTime",task.getTask_time());
            bundle.putString("taskType",task.getTask_type());
            bundle.putString("taskDetails",task.getTask_details());
            bundle.putString("taskID",task.getTask_id());
            bundle.putString("taskAlarm",task.getAlarm_status()+"");
            return bundle;
        }
    }
}