package br.com.android.portfolio.whatsappclone.whatsappclone.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.android.portfolio.whatsappclone.whatsappclone.R;
import br.com.android.portfolio.whatsappclone.whatsappclone.activity.ConversationActivity;
import br.com.android.portfolio.whatsappclone.whatsappclone.adapter.ContactAdapter;
import br.com.android.portfolio.whatsappclone.whatsappclone.helper.Preferences;
import br.com.android.portfolio.whatsappclone.whatsappclone.model.Contact;
import br.com.android.portfolio.whatsappclone.whatsappclone.utils.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contact> contatos;
    private ValueEventListener valueEventListenerContatos;
    private DatabaseReference firebase;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Instanciadndo a lista de contatos
        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);
        listView = (ListView) view.findViewById(R.id.lv_contatos);

        adapter = new ContactAdapter(getContext(), contatos);
        loadContacts();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);

                //Recuperando os dados do usuario para serem passados
                Contact contato = contatos.get(i);

                //Passando dados para a activity ConversasActivity
                intent.putExtra("nome", contato.getName());
                intent.putExtra("email", contato.getEmail());

                startActivity(intent);
            }
        });


        return view;
    }

    public void loadContacts() {

        Preferences preferencias = new Preferences(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();
        firebase =    Util.getFirebase()
                .child("contatos")
                .child(identificadorUsuarioLogado);

        if ( firebase != null ){

            valueEventListenerContatos = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    contatos.clear();

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Contact contato = dados.getValue(Contact.class);
                        if (contato != null) {
                            contatos.add(contato);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }else{
            Toast.makeText(getContext(), R.string.errorConnection, Toast.LENGTH_SHORT).show();
        }
    }

}
