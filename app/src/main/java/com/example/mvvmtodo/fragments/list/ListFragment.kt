package com.example.mvvmtodo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmtodo.R
import com.example.mvvmtodo.data.ToDoViewModel
import com.example.mvvmtodo.data.models.ToDoData
import com.example.mvvmtodo.databinding.FragmentListBinding
import com.example.mvvmtodo.fragments.SharedViewModel
import com.example.mvvmtodo.fragments.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    //ViewModelのスコープ == このプロパティを定義したFragment(=ListFragment)のスコープ
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    //ライブラリーによる自動生成クラス
    private var _binding : FragmentListBinding? = null
    //アクセス用プロパティ
    // get() はkotlinでは通常省略されるgetterの拡張版。プロック内に好きなコードを書いてgetter呼び出し時の挙動を自由に設定できる
    //今回の場合は、_bindingのgetterを返すように拡張している
    private val binding get() = _binding!!

    //byはプロパティの委譲、この場合adapterプロパティのゲッターセッターはListAdapterインスタンスに任されている
    // （なのでListAdapterには明示的にゲッターやセッターの定義が必要）
    //lazyは一度ゲッターによって初期化されたら、二度目以降は同じインスタンスを返す
    private val adapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Data binding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        //setup recycerView
        setupRecyclerView()

        //LiveDataの監視
        //以下の部分は、新規ToDo追加のたびに呼ばれる。（追加後、追加画面からリスト画面に遷移する。遷移時、onCreateViewが呼ばれる（このブロック））
        //Observeブロック内のdataは List<ToDoData>
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        //requireActivity()はnullを返さない
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        //set animator
        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }

        swipeToDelete(recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteItem(deletedItem)

                //animator を動かすために必要
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(requireContext(), "Successfully Removed: '${deletedItem.title}'", Toast.LENGTH_SHORT).show()

                //restore deleted item
                restoreDeletedDate(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedDate(view: View, deletedItem: ToDoData, position: Int) {
        //Snackbar will try and find a parent view to hold Snackbar's view from the value given to view.
        val snackBar = Snackbar.make(
                view, "Deleted '${deletedItem.title}'", Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)

            //animator を動かすために必要
            adapter.notifyItemChanged(position)
        }
        snackBar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_delete_all -> confirmItemRemove()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmItemRemove() {
        val builder = AlertDialog.Builder(requireContext())
            .setPositiveButton("Yes") { _, _ ->
                mToDoViewModel.deleteAll()
                Toast.makeText(requireContext(), "Successfully removed everything!", Toast.LENGTH_SHORT).show()
            }
        builder.setNegativeButton("No") { _, _ ->
            //処理なし
        }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        //メモリリークを防ぐ
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"

        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list?.let {
                adapter.setData(it)
            }
        })

    }
}