package com.example.mvvmtodo.fragments.list

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * ItemTouchHelper.SimpleCallbackを継承したクラスを作成し、onMovedをオーバーライドしておくことで、
 * 呼び出しもとで不必要なonMovedのオーバーライドを避けることができ、コードが簡潔になる
 */
abstract class SwipeToDelete: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }
}