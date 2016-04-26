package com.adadapted.sdktestapp.ui.todo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.adadapted.android.sdk.ui.view.AaZoneView;
import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;
import com.adadapted.sdktestapp.ui.todo.activity.TodoListDetailActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoListsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodoListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoListsFragment extends ListFragment implements TodoListManager.Listener {
    private static final String TAG = TodoListsFragment.class.getName();

    private OnFragmentInteractionListener mListener;

    private ArrayAdapter<TodoList> adapter;
    private List<TodoList> lists;
    private AaZoneView aaZoneView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TodoListsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoListsFragment newInstance() {
        TodoListsFragment fragment = new TodoListsFragment();
        return fragment;
    }

    public TodoListsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lists = TodoListManager.getInstance(getActivity()).getLists();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, lists);

        aaZoneView = new AaZoneView(getActivity());
        aaZoneView.init("100680", R.layout.aa_default_json_ad_zone);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_lists, container, false);

        ListView listView = (ListView)view.findViewById(android.R.id.list);
        listView.addFooterView(aaZoneView);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        TodoListManager.getInstance(getActivity()).addListener(this);
        aaZoneView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();

        TodoListManager.getInstance(getActivity()).removeListener(this);
        aaZoneView.onStop();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TodoList list = lists.get(position);

        Intent intent = new Intent(getActivity(), TodoListDetailActivity.class);
        intent.putExtra(TodoListDetailActivity.TODO_LIST_ID, list.getId());

        startActivity(intent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTodoListsAvailable() {
        lists = TodoListManager.getInstance(getActivity()).getLists();
        adapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
