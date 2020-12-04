package com.example.mvvmtodo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mvvmtodo.R
import com.example.mvvmtodo.data.ToDoViewModel
import com.example.mvvmtodo.data.models.ToDoData
import com.example.mvvmtodo.databinding.FragmentUpdateBinding
import com.example.mvvmtodo.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*

class UpdateFragment : Fragment() {

    //UpdateFragmentArgsはプログインによろ自動生成
    //actionListFragmentToUpdateFragmentからの引数を受け取る
    private val args by navArgs<UpdateFragmentArgs>()

    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        setHasOptionsMenu(true)

        //bindingのcurrentPrioritiesSpinnerはlayout fileのspinnerを表す
        binding.currentPrioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener

        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemove()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmItemRemove() {
        val builder = AlertDialog.Builder(requireContext())
            .setPositiveButton("Yes") { _, _ ->
                mToDoViewModel.deleteItem(args.currentitem)
                Toast.makeText(requireContext(), "Successfully removed: ${args.currentitem.title}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
        builder.setNegativeButton("No") { _, _ ->
            //処理なし
        }
        builder.setTitle("Delete ${args.currentitem.title}?")
        builder.setMessage("Are you sure you want to remove ${args.currentitem.title}?")
        builder.create().show()
    }

    private fun updateItem() {
        val title = current_title_et.text.toString()
        val description = current_description_et.text.toString()
        val getPriority = current_priorities_spinner.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val updatedItem = ToDoData(
                args.currentitem.id,
                title,
                mSharedViewModel.parsePriority(getPriority),
                description
            )
            mToDoViewModel.updateData(updatedItem)
            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()

            //navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fil out all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //to prevent memory leak. it is very important!!(when the life cycle ends)
        _binding = null
    }
}