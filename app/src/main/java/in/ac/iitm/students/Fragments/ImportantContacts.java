package in.ac.iitm.students.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.ac.iitm.students.Adapters.ContactsAdapter;
import in.ac.iitm.students.Adapters.EventsAdapter;
import in.ac.iitm.students.Objects.Contacts;
import in.ac.iitm.students.R;
import in.ac.iitm.students.Utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImportantContacts extends Fragment {
     public static View ExpandedLayout;
    RecyclerView mRecyclerView;
    String ISEXPANDED = "isExpanded0";

    public ImportantContacts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.imprtant_contacts_fragment, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        ArrayList<Contacts> Contacts=new ArrayList<Contacts>();
        Contacts.add(new Contacts("04422578888","Medical Emergency"));
        Contacts.add(new Contacts("04422579999","Security"));
        Contacts.add(new Contacts("04422575555","Tele Counselling"));
        Contacts.add(new Contacts("04422575987","Lan Complants"));
        Contacts.add(new Contacts("04422578187","Electrical Complants"));
        Contacts.add(new Contacts("04422578504","CCW Office"));



        Utils.saveprefBool(ISEXPANDED, false, getActivity());

        mRecyclerView.setAdapter(new ContactsAdapter(getActivity(),Contacts));
        // Inflate the layout for this fragment
        return v;

    }

    public void dialPhoneNumber(String phoneNumber) {
               Intent intent = new Intent(Intent.ACTION_DIAL);
               intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                       startActivity(intent);
                    }
            }
}
