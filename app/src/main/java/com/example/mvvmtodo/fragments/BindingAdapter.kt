package com.example.mvvmtodo.fragments

import android.os.Build
import android.view.View
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.mvvmtodo.R
import com.example.mvvmtodo.data.models.Priority
import com.example.mvvmtodo.data.models.ToDoData
import com.example.mvvmtodo.fragments.list.ListFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BindingAdapter {

    companion object {

        //BindingAdapter(自作のattribute(属性))
        //この自作の属性に指定した値は、このメソッド(navigateToAddFragment)の第二引数以降になる
        @BindingAdapter("android:navigateToAddFragment")
        //staticメソッドを表す
        @JvmStatic
        fun navigateToAddFragment(view: FloatingActionButton, navigate: Boolean) {
            view.setOnClickListener {
                if (navigate) {
                    view.findNavController().navigate(R.id.action_listFragment_to_addFragment)
                }
            }
        }

        /**
         * このメソッドをレイアウトファイルの属性で指定することで、FragmentでLiveDataを監視する必要がなくなる。
         * LiveDataの監視はレイアウトファイル内で行う。
         */
        @BindingAdapter("android:emptyDatabse")
        @JvmStatic
        fun emptyDatabase(view: View, emptyDatabase: MutableLiveData<Boolean>) {
            when(emptyDatabase.value) {
                true -> view.visibility = View.VISIBLE
                false -> view.visibility = View.INVISIBLE
            }
        }

        @BindingAdapter("android:parsePriorityToInt")
        @JvmStatic
        fun parsePriorityToInt(spinner: Spinner, priority: Priority) {
            when(priority) {
                Priority.HIGH -> {spinner.setSelection(0)}
                Priority.MEDIUM -> {spinner.setSelection(1)}
                Priority.LOW -> {spinner.setSelection(2)}
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)  //API level 23以上
        @BindingAdapter("android:parsePriorityColor")
        @JvmStatic
        fun parsePriorityColor(cardView: CardView, priority: Priority) {
            when (priority) {
                Priority.HIGH -> {
                    cardView.setCardBackgroundColor(cardView.context.getColor(android.R.color.holo_red_light))
                }
                Priority.MEDIUM -> {
                    cardView.setCardBackgroundColor(cardView.context.getColor(android.R.color.holo_orange_light))
                }
                Priority.LOW -> {
                    cardView.setCardBackgroundColor(cardView.context.getColor(android.R.color.holo_green_light))
                }
            }
        }

        @BindingAdapter("android:sendDataToUpdateFragment")
        @JvmStatic
        fun sendDataToUpdateFragment(view: ConstraintLayout, currentItem: ToDoData) {
            view.setOnClickListener {
                //safe args
                //list から updateのactionの際に、argumentsを設定
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
                view.findNavController().navigate(action)
            }
        }
    }
}