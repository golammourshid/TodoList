package edu.univdhaka.cse2216.myplane.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.app.Fragment;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.univdhaka.cse2216.myplane.R;
import edu.univdhaka.cse2216.myplane.domain.Tasks;
import edu.univdhaka.cse2216.myplane.utils.Alarm;
import edu.univdhaka.cse2216.myplane.utils.DataBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;

public class AddNew extends Fragment implements View.OnClickListener {

    private ImageButton clockButton,calenderButton;
    private EditText clockText,calenderText,taskName,taskDetails;
    //private Button button;
    private ImageButton backbtn,okbtn;
    private String[] taskstypes;
    private Spinner spinner;
    private Switch alarmSwitch;
    private DataBase dataBase;
    private SQLiteDatabase sqLiteDatabase;
    private Bundle bundle;
    private String activityType,givenHourMinute,givenDate;
    private ArrayAdapter<String> adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.addnew, container, false);
        bindWidgets(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityType.equalsIgnoreCase("edit")) {
            ((MainActivity)getActivity()).setActionBarTitle("Edit Task");}
        else {
            ((MainActivity)getActivity()).setActionBarTitle("New Task"); }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void bindWidgets(View view)
    {
        dataBase = new DataBase(getContext());
        sqLiteDatabase = dataBase.getWritableDatabase();
        taskName = (EditText) view.findViewById(R.id.taskName);
        clockButton = (ImageButton) view.findViewById(R.id.clock);
        calenderButton = (ImageButton) view.findViewById(R.id.calender);
        clockText = (EditText) view.findViewById(R.id.clockText);
        calenderText = (EditText) view.findViewById(R.id.calenderText);
        alarmSwitch = (Switch) view.findViewById(R.id.alarmSwitch);
        //button = view.findViewById(R.id.addButton);
        backbtn = view.findViewById(R.id.backbtnid);
        okbtn = view.findViewById(R.id.okbtnid);
        taskstypes = getResources().getStringArray(R.array.Lists);
        spinner = view.findViewById(R.id.typeID);
        taskDetails = (EditText)view.findViewById(R.id.task_details);


        clockButton.setOnClickListener(this);
        calenderButton.setOnClickListener(this);
        calenderText.setOnClickListener(this);
        clockText.setOnClickListener(this);
        //button.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        okbtn.setOnClickListener(this);
        adapter = new ArrayAdapter<String>(getContext(),R.layout.list_row,R.id.typeRowID,taskstypes);
        spinner.setAdapter(adapter);

        bundle  = getArguments();
        activityType = String.valueOf(bundle.getString("ActivityType"));
        if (activityType.equalsIgnoreCase("edit")) {
            retriveData();}
        return;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {

        /********** to set date **********/
        hideKeyboard((MainActivity)getActivity());

        if (view.getId() == R.id.calender  || view.getId() == R.id.calenderText)
        {

            Calendar cal = Calendar.getInstance();
            int year1 = cal.get(Calendar.YEAR);
            int month1 = cal.get(Calendar.MONTH);
            int day1 = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String m,d;
                            if (month < 9)
                             m = "0"+(month+1);
                            else m = ""+(month+1);
                            if(dayOfMonth<=9) d="0"+dayOfMonth;
                            else d = ""+dayOfMonth;

                            calenderText.setText(d+ "/" +m+ "/" + year);
                            givenDate = year+"/"+m+"/"+d;

                            //Toast.makeText(getContext(),givenDate,Toast.LENGTH_LONG).show();
                        }
                    },year1, month1, day1);
            dialog.show();
        }

        /********** to set time **********/
        if (view.getId() == R.id.clock || view.getId() == R.id.clockText)
        {
            Calendar calendar = Calendar.getInstance();
            final int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(getContext(),AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time = updateTime(hourOfDay,minute);
                            clockText.setText(time);
                            givenHourMinute = hourOfDay+":"+minute+":"+10;
                        }
                    },hour,minute,false);
            dialog.show();
        }

        /********** saving the task **********/
        if ( view.getId() == R.id.okbtnid) {
            hideKeyboard((MainActivity)getActivity());
            saveTask();
        }
        if (view.getId() == R.id.backbtnid)
        {
            getFragmentManager().popBackStackImmediate();
        }

    }
    private String updateTime(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) { hours -= 12;
            timeSet = "pm";
        }
        else if (hours == 0) { hours += 12;
            timeSet = "am";
        }
        else if (hours == 12) timeSet = "pm";
        else timeSet = "am";

        String minutes = "";
        if (mins < 10) minutes = "0" + mins;
        else minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();
        return aTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveTask()
    {
        if (validateInput()) {
            String task = taskName.getText().toString();
            String taskDate = calenderText.getText().toString();
            String taskTime = clockText.getText().toString();
            String taskType = spinner.getSelectedItem().toString();
            String task_details = taskDetails.getText().toString();
            int al = 0;
            if (alarmSwitch.isChecked()) {
                al = 1;
            }
            /************ if new task ***********/
            if (activityType.equalsIgnoreCase("newAdd")) {
                Tasks newTask = new Tasks(al, "0", task, taskDate, taskTime, taskType, task_details);
                long flag = dataBase.insertData(newTask);
                Cursor cursor = dataBase.getAllData();
                cursor.moveToLast();
                if (al == 1) {
                    ((MainActivity)getActivity()).setAlarm(getTimeInMilli(),Integer.parseInt(cursor.getString(0)));
                }
            }
            /************ for edit ***********/
            else{
                Tasks tasks = new Tasks();
                tasks.setTask_name(task);
                tasks.setTask_date(taskDate);
                tasks.setTask_time(taskTime);
                tasks.setIsAlarm(al);
                tasks.setTask_type(taskType);
                tasks.setTask_details(task_details);
                tasks.setTask_id(String.valueOf(bundle.getString("taskID")));
                dataBase.updateData(tasks);
                if (al==1) ((MainActivity)getActivity()).setAlarm(getTimeInMilli(),Integer.parseInt(tasks.getTask_id()));
            }

            FragmentTransaction transaction=getFragmentManager().beginTransaction();
            transaction.replace(R.id.parentId,new HomeFragment());
            transaction.commit();
        }
    }
    /********** checks if any field is empty ********/
    private boolean validateInput() {
        boolean allInputsValid = true;
        for(EditText input
                : new EditText[]{taskName, calenderText, clockText, taskDetails}) {
            if (input.getText().toString().isEmpty()) {
                showError(input, R.string.required);
                allInputsValid = false;
            }
        }
        return allInputsValid;
    }
    private void showError(EditText field, int messageRes) {
        field.setError(getString(messageRes));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void retriveData()
    {
        String dateString = String.valueOf(bundle.getString("taskDate"));
        String timeString = String.valueOf(bundle.getString("taskTime"));
        String typeString = String.valueOf(bundle.getString("taskType"));
        String alarmString = String.valueOf(bundle.getString("taskAlarm"));
        taskName.setText(String.valueOf(bundle.getString("taskName")));
        calenderText.setText(dateString);
        clockText.setText(timeString);
        givenDate = dateString.substring(6)+"/"+dateString.substring(3,6)+dateString.substring(0,2);
        //Toast.makeText(getContext(),dateString+"  =  "+timeString,Toast.LENGTH_LONG).show();

        int point = timeString.indexOf(':');
        String hour = timeString.substring(0,point);
        String pmCheck = timeString.substring(timeString.length()-2);
        if (pmCheck.equalsIgnoreCase("pm")) {
            hour = ""+(Integer.parseInt(hour)+12);
        }
        hour = hour+timeString.substring(point,point+3)+":"+10;
        givenHourMinute = hour;
        taskDetails.setText(String.valueOf(bundle.getString("taskDetails")));
        spinner.setSelection(adapter.getPosition(typeString));
        if (alarmString.equalsIgnoreCase("1"))
            alarmSwitch.setChecked(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private long getTimeInMilli()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String givenTime = givenDate+" "+givenHourMinute;
        Date date = null;
        Date currentTime = Calendar.getInstance().getTime();
        try { date = sdf.parse(givenTime);
        } catch (ParseException e) { e.printStackTrace(); }
        return date.getTime() - currentTime.getTime();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}