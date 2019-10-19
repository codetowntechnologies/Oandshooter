package com.example.oandshooter.view;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oandshooter.R;
import com.example.oandshooter.database.DatabaseHelper;
import com.example.oandshooter.database.model.Note;
import com.example.oandshooter.utils.MyData;
import com.example.oandshooter.utils.MyDividerItemDecoration;
import com.example.oandshooter.utils.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.oandshooter.utils.MyData.as;

public class MainActivity extends AppCompatActivity {
    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private RelativeLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;
    private DatabaseHelper db;
    private Button startProjectionButton;
    private Button stopProjectionButton;
    private static MediaProjection sMediaProjection;
    private MediaProjectionManager mProjectionManager;
    private Handler mHandler;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;
    private Display mDisplay;
    private static final int REQUEST_SCREENSHOT=59706;
    Handler handler = new Handler();
    boolean clicked=false;
    private EditText time;
    Context context;
    public Button add;
    //SharedPreferences pref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        coordinatorLayout  = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);
        startProjectionButton=findViewById(R.id.buttonStartProjection);
        stopProjectionButton=findViewById(R.id.buttonStopProjection);
        time=findViewById(R.id.et_inputTime);

        MyData.context = getApplicationContext();


      /*  final int as = Integer.parseInt(time.getText().toString());
        MyData.as = as ;
        Intent startProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
        startProjectionIntent.putExtra("as",as);
        startActivity(startProjectionIntent);*/
        /*Intent startProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
        startActivity(startProjectionIntent);*/


     /*   Intent serviceIntent = new Intent(this, ScreenshotService.class);
        Log.e("serviceIntent","serviceIntentrunning");
        startService(serviceIntent);*/





      /*  Intent startProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
        startActivity(startProjectionIntent);*/

        //Button add = (Button)findViewById(R.id.add);



/*
        Cursor rs = db.getData(Value);
        String phon = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PHONE));
*/

        startProjectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* final int as = Integer.parseInt(time.getText().toString());
                MyData.as = as ;
                Intent startProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
                startProjectionIntent.putExtra("as",as);
                startActivity(startProjectionIntent);*/
               Log.e("Hello","Taking Screenshot");

            }
        });


        stopProjectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            /*    Intent stopProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
                stopProjectionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stopService(MyData.i);*/
                    processStopService(ScreenshotService.TAG);
                  //  isMyServiceRunning(ScreenshotService.class);
                }
            });


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            showNoteDialog(false, null, -1);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }


        /*pref = getApplicationContext().getSharedPreferences("id",MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("key_name", as);
        //Log.e("option ==== "+editor,"editor.putInt");
        String timee = pref.getString("key_name", null);
        Log.e("time========", String.valueOf(timee));*/


       /* add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog();
            }
        });*/

   db = new DatabaseHelper(this);
        notesList.addAll(db.getAllNotes());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);

            }
        });

        mAdapter = new NotesAdapter(this,notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }



    private void processStopService(final String tag) {
        Intent intent = new Intent(getApplicationContext(), ScreenshotService.class);
        intent.addCategory(tag);
        stopService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void stopProjection() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                }
            }
        });
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String note,int time) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note,time);

        // get the newly inserted note from db
        Note n = db.getNote(id);



        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }


    /////////////////////
    private void createNotee(String note,int time) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNotee(note,time);

        // get the newly inserted note from db
        Note n = db.getNote(id);



        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }
    ////////////////////

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String note, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setNote(note);
        //n.setTime(t);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit","Delete"};
        //CharSequence colors[] = new CharSequence[]{"Edit"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }


    ///////////////////////
    private void showActionsDialogg(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit","Delete"};
        //CharSequence colors[] = new CharSequence[]{"Edit"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialogg(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }
    //////////////////////


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */


    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        final View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);
        final RadioButton[] radioSexButton = new RadioButton[1];

        TextView textView = view.findViewById(R.id.textView2);
        final EditText inputNote = view.findViewById(R.id.note);
        final EditText editText_inputemail = view.findViewById(R.id.inputEmail);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        final TextView readioText = view.findViewById(R.id.showstatus);
      //  final RadioGroup selectNetworkRadio =view.findViewById(R.id.selectNetwork);


        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
            editText_inputemail.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            editText_inputemail.setText(Integer.toString(note.getTime()));
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered

                String inputNoteemail1=inputNote.getText().toString();
                MyData.email=inputNoteemail1;
                if (TextUtils.isEmpty(inputNoteemail1)) {

                    // svc.processImage(newPng);
                    Toast.makeText(MainActivity.this, "Enter Email!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    final int as = Integer.parseInt(editText_inputemail.getText().toString());
                    int minute = 60000;
                    int j= as*minute;
                    MyData.as = j ;
                    String inputNoteemail2=inputNote.getText().toString();
                    MyData.email=inputNoteemail2;
                    updateNote(MyData.email, position);
                    Intent startProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
                    startProjectionIntent.putExtra("as",as);
                    startActivity(startProjectionIntent);
                } else {
                    // create new note
                    editText_inputemail.getText().toString().trim();
                    int main_time = Integer.parseInt(editText_inputemail.getText().toString());

                   //final int ass = Integer.parseInt(editText_inputemail.getText().toString());

                    SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("your_int_key", main_time);
                    editor.commit();


                    Log.e("asdfgg=======", String.valueOf(as));
                    MyData.as = main_time*60000;
                    Log.e("ttttt=======", String.valueOf(MyData.as));
                    String inputNoteemai3=inputNote.getText().toString();
                    MyData.email=inputNoteemai3;
                    createNote(MyData.email, MyData.as);
                    Intent startProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
                    startProjectionIntent.putExtra("as",as);
                    startActivity(startProjectionIntent);


                }
            }
        });
    }
    ////////////////////////

    private void showNoteDialogg(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        final View view = layoutInflaterAndroid.inflate(R.layout.email_id_alert, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);
        final RadioButton[] radioSexButton = new RadioButton[1];

        final EditText inputNote = view.findViewById(R.id.editText);
       // dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));
        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
            //editText_inputemail.setVisibility(View.GONE);
            //editText_inputemail.setText(Integer.toString(note.getTime()));
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered

                String inputNoteemail1=inputNote.getText().toString();
                MyData.email=inputNoteemail1;
                if (TextUtils.isEmpty(inputNoteemail1)) {

                    // svc.processImage(newPng);
                    Toast.makeText(MainActivity.this, "Enter Email!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    /*final int as = Integer.parseInt(editText_inputemail.getText().toString());
                    int minute = 60000;
                    int j= as*minute;
                    MyData.as = j ;*/
                    String inputNoteemail2=inputNote.getText().toString();
                    MyData.email=inputNoteemail2;
                    updateNote(MyData.email, position);
                    Intent startProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
                    startProjectionIntent.putExtra("as",as);
                    startActivity(startProjectionIntent);
                } else {
                    String inputNoteemai3=inputNote.getText().toString();
                    MyData.email=inputNoteemai3;
                    SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    int myIntValue = sp.getInt("your_int_key", -1);
                    int minute = (int) TimeUnit.MINUTES.toMillis(myIntValue);
                    createNotee(MyData.email,minute);
                    Intent startProjectionIntent =new Intent(MainActivity.this, ScreenShotActivityMain.class);
                    startProjectionIntent.putExtra("as",as);
                    startActivity(startProjectionIntent);


                }
            }
        });
    }

    ////////////////////////
    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();


        if(id == R.id.action_favorite){
            showNoteDialogg(false, null, -1);
            /*Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;*/
        }


        return super.onOptionsItemSelected(item);



    }

    /* @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);

       int id = item.getItemId();
    }*/
}
