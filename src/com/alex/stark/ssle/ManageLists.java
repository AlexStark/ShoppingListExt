package com.alex.stark.ssle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ManageLists extends Activity {

	SQLiteDatabase db;
	
	int activeCategory = 0;
	LinearLayout linear;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState != null) {
			activeCategory = new Integer(savedInstanceState.getString("category_id"));
		} else {
			Bundle extras = getIntent().getExtras();
			if(extras != null) {
				String category_id = extras.getString("category_id");
				if(category_id != null) {
					activeCategory = new Integer(category_id);
				}
			}
		}
		
		if(activeCategory == 0) {
			
			Log.w("WARNING", "No active ID found");
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.categories);
		
        db = ((SQLiteDatabase) Registry.get(Registry.DATABASE));
    	
    	((Button) findViewById(R.id.button_add)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showAddListDialog();
			}
		});
    	
    	updateView();
	}
	
	public void goBack(int categoryId) {
		
		setResult(categoryId);
		finish();
	}
	
	public void updateView() {
		
		linear = ((LinearLayout) findViewById(R.id.ContentLinearLayout));
		linear.removeAllViews();
		
		Cursor c = db.rawQuery("SELECT * FROM categories;", null);
		
		if(c.getCount() > 0) {
		
			c.moveToFirst();
			
			do {
				
				int id = new Integer(c.getString(c.getColumnIndex("id")));
				String name = c.getString(c.getColumnIndex("name"));
				
				if(id == 1 && name == null) {
	        		
	        		name = getText(R.string.app_name).toString();
	        	}
				
				List l = new List(this);
				if(id == activeCategory) {
					l.setActive(true);
				}
				l.setId(id);
				l.setName(name);
				l.create();
				
				linear.addView(l);
				
			} while (c.moveToNext());
		}
	}
	
    void showToast(String message) {
    	
    	showToast(message, Toast.LENGTH_SHORT);
    }
    
    private void showToast(String message, int duration) {
    	
    	Toast toast = Toast.makeText(this, message, duration);
    	toast.show();
    }
    
    private void createList(String name) {
    	
    	ContentValues cv = new ContentValues();
    	cv.put("name", name);
    	
    	db.insert("categories", null, cv);
    	updateView();
    }
    
    public void showAddListDialog() {
    	
    	final EditText et = new EditText(this);
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setView(et);
		b.setPositiveButton(getText(R.string.button_ok), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String value = et.getText().toString().trim();
				
				if(value.length() > 0) {
					
					showToast(getText(R.string.added) + ": " + value);
					createList(value);
				} else {
					
					showToast(getText(R.string.err_elem_not_entered).toString());
				}
				dialog.cancel();
			}
		});
		
		b.setNegativeButton(getText(R.string.button_cancel), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		}); 
		
		AlertDialog dialog = b.create();
		
		dialog.setTitle(getText(R.string.dialog_new_list_title));
		dialog.setMessage(getText(R.string.dialog_new_list_message));
		
		dialog.show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu_categories, menu);
        
    	return true;
    }
    
    public boolean onOptionsItemSelected (MenuItem item){

    	if(item.getItemId() ==  R.id.menu_add_list) {
    		
    		showAddListDialog();
    	}

    	if(item.getItemId() == R.id.menu_back) {

    		goBack(activeCategory);
    	}

    	return false;
    }
}

