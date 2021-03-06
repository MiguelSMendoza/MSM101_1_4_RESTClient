package es.netrunners;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class RESTClientActivity extends ListActivity {

    String[] from = new String[]{"Name", "Surname", "Age"};
    int[] to = new int[]{R.id.name, R.id.surname, R.id.age};

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        fillList();
        registerForContextMenu(this.getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            @SuppressWarnings("unchecked")
            HashMap<String, String> item = (HashMap<String, String>) this
                    .getListAdapter().getItem(info.position);

            menu.setHeaderTitle(item.get("Name") + " " + item.get("Surname"));
            String[] menuItems = getResources()
                    .getStringArray(R.array.listmenu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        @SuppressWarnings("unchecked")
        HashMap<String, String> itm = (HashMap<String, String>) this
                .getListAdapter().getItem(info.position);
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case 0:
                showEditClientDialog(Integer.parseInt(itm.get("ID")),
                        itm.get("Name"), itm.get("Surname"), itm.get("Age"));
                return true;
            case 1:
                showConfirmDialog(info.position);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addClient:
                showNewClientDialog();
                return true;
        }
        return false;
    }

    private void showNewClientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog, null);
        builder.setView(textEntryView);
        builder.setTitle("New Client")
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText name = (EditText) textEntryView
                                        .findViewById(R.id.newname);
                                EditText surname = (EditText) textEntryView
                                        .findViewById(R.id.newsurname);
                                EditText age = (EditText) textEntryView
                                        .findViewById(R.id.newage);
                                addClient(name.getText().toString(), surname
                                        .getText().toString(), age.getText()
                                        .toString());

                            }

                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showEditClientDialog(final int ID, String name,
                                      String surname, String age) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog, null);
        builder.setView(textEntryView);
        EditText Name = (EditText) textEntryView.findViewById(R.id.newname);
        EditText Surname = (EditText) textEntryView
                .findViewById(R.id.newsurname);
        EditText Age = (EditText) textEntryView.findViewById(R.id.newage);
        Name.setText(name);
        Surname.setText(surname);
        Age.setText(age);
        builder.setTitle("Edit Client")
                .setCancelable(false)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText name = (EditText) textEntryView
                                        .findViewById(R.id.newname);
                                EditText surname = (EditText) textEntryView
                                        .findViewById(R.id.newsurname);
                                EditText age = (EditText) textEntryView
                                        .findViewById(R.id.newage);
                                editClient(ID, name.getText().toString(),
                                        surname.getText().toString(), age
                                                .getText().toString());
                            }

                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showConfirmDialog(int position) {
        @SuppressWarnings("unchecked")
        final HashMap<String, String> itm = (HashMap<String, String>) this
                .getListAdapter().getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Are you sure you want to DELETE " + itm.get("Name") + " "
                        + itm.get("Surname") + "?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                deleteClient(Integer.parseInt(itm.get("ID")));

                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    protected void deleteClient(final int ID) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpDelete del = new HttpDelete(
                        "http://services.netrunners.es/API/Clients/Client/" + ID);

                del.setHeader("content-type", "application/json");

                try {
                    HttpResponse resp = httpClient.execute(del);
                    String respStr = EntityUtils.toString(resp.getEntity());

                    if (Integer.parseInt(respStr) > 0) {
                        return true;
                    }
                } catch (Exception ex) {
                    return false;
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getApplicationContext(),
                            "Deleted Succesfully !!", Toast.LENGTH_LONG).show();
                    fillList();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();


    }

    protected void editClient(final int iD, final String name, final String surname, final String age) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPut put = new HttpPut("http://services.netrunners.es/API/Clients/Client/" + iD);
                put.addHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                try {
                    // Construimos el objeto cliente en formato JSON
                    JSONObject data = new JSONObject();

                    data.put("Name", name);
                    data.put("Surname", surname);
                    data.put("Age", age);

                    String json = data.toString();
                    Log.e("DATA", json);
                    StringEntity entity = new StringEntity(json);
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    put.setEntity(entity);

                    HttpResponse resp = httpClient.execute(put);
                    String respStr = EntityUtils.toString(resp.getEntity());
                    return respStr;
                } catch (Exception ex) {
                    return ex.getMessage() + ex.getClass();
                }
            }

            @Override
            protected void onPostExecute(String strResponse) {
                if (Integer.parseInt(strResponse) > 0) {
                    Toast.makeText(getApplicationContext(),
                            "Editted Succesfully !!",
                            Toast.LENGTH_LONG).show();
                    fillList();
                } else {
                    Toast.makeText(getApplicationContext(),
                            strResponse, Toast.LENGTH_LONG).show();
                }

            }
        }.execute();


    }

    private void addClient(final String name, final String surname, final String age) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost post = new HttpPost("http://services.netrunners.es/API/Clients/Client");

                post.setHeader("content-type", "application/json");
                try {
                    // Build JSON Object
                    JSONObject data = new JSONObject();

                    data.put("Name", name);
                    data.put("Surname", surname);
                    data.put("Age", age);

                    StringEntity entity;

                    entity = new StringEntity(data.toString());

                    post.setEntity(entity);
                } catch (UnsupportedEncodingException e) {
                    return e.getMessage();
                } catch (JSONException e) {
                    return e.getMessage();
                }

                HttpResponse resp;
                try {
                    resp = httpClient.execute(post);

                    String respStr = EntityUtils.toString(resp.getEntity());

                    return respStr;
                } catch (ClientProtocolException e) {
                    return e.getMessage();
                } catch (IOException e) {
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String strResponse) {
                if (Integer.parseInt(strResponse) > 0) {
                    Toast.makeText(getApplicationContext(),
                            "Added Succesfully !!",
                            Toast.LENGTH_LONG).show();
                    fillList();
                } else {
                    Toast.makeText(getApplicationContext(),
                            strResponse, Toast.LENGTH_LONG).show();
                }

            }
        }.execute();

    }


    private void fillList() {

        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... params) {
                JSONArray respJSON = null;
                HttpClient httpClient = new DefaultHttpClient();

                HttpGet get = new HttpGet("http://services.netrunners.es/API/Clients");

                get.setHeader("content-type", "application/json");

                try {
                    HttpResponse resp = httpClient.execute(get);
                    String respStr = EntityUtils.toString(resp.getEntity());
                    return respStr;
                } catch (Exception e) {
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String respStr) {
                JSONArray respJSON = null;
                try {
                    respJSON = new JSONArray(respStr);
                    Client[] listClients = new Client[respJSON.length()];
                    for (int i = 0; i < respJSON.length(); i++) {
                        JSONObject obj = null;
                        obj = respJSON.getJSONObject(i);
                        Client cli = new Client();
                        cli.setID(obj.getInt("ID"));
                        cli.setName(obj.getString("Name"));
                        cli.setSurname(obj.getString("Surname"));
                        cli.setAge(obj.getInt("Age"));
                        listClients[i] = cli;
                    }
                    ArrayList<HashMap<String, String>> Clients = new ArrayList<HashMap<String, String>>();
                    for (Client client : listClients) {

                        HashMap<String, String> clientData = new HashMap<String, String>();

                        clientData.put("ID", String.valueOf(client.getID()));
                        clientData.put(from[0], client.getName());
                        clientData.put(from[1], client.getSurname());
                        clientData.put(from[2], String.valueOf(client.getAge()));

                        Clients.add(clientData);
                    }

                    SimpleAdapter ListAdapter = new SimpleAdapter(getApplicationContext(), Clients,
                            R.layout.row, from, to);
                    setListAdapter(ListAdapter);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }

}
