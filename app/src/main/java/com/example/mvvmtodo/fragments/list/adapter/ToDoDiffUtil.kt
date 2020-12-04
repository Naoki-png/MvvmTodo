package com.example.mvvmtodo.fragments.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mvvmtodo.data.models.ToDoData

/**
 * DiffUtilクラスは、recyclerViewのnotifyDataSetChangedなどのパフォーマンスが悪いメソッドの代替となる
 */
class ToDoDiffUtil(
    private val oldList: List<ToDoData>,
    private val newList: List<ToDoData>
): DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    /**
     * このメソッドは比較するアイテムがIDを持っている場合、IDで同じかどうかを判定する
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //===はオブジェクト比較に使われる。==はjavaのequals()と同じ
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    /**
     * このメソッドはareItemsTheSameがtrueの時だけ呼ばれる。
     * itemの中身を比較して、同じかどうかを判定するメソッド（今回はアイテムの中身全てを比較）
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].title == newList[newItemPosition].title
                && oldList[oldItemPosition].description == newList[newItemPosition].description
                && oldList[oldItemPosition].priority == newList[newItemPosition].priority
    }

}