package com.skoumal.grimoire.wand.recyclerview.diff

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper differ interface for asynchronously updating your RecyclerView's Adapters. It often takes
 * the responsibility of holding the list for the current adapter, whilst having the adapter
 * implementing this interface.
 *
 * Common usage would be:
 *
 * ```kotlin
 *  class MyAdapter(
 *      differ: DiffUtil.ItemCallback<MyData>
 *  ) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
 *      AdapterListDiffer<MyData> by SimpleAdapterListDiffer(differ) {
 *
 *      // ...
 *
 *      override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
 *          val data = getItemAt(position)
 *          if (data != null) {
 *              holder.onBindData()
 *          } // else bind it to placeholder
 *      }
 *
 *      override fun getItemCount(): Int {
 *          return currentItemCount
 *      }
 *
 *  }
 * ```
 *
 * ---
 *
 * For updating such adapter you are strongly suggested to use [submitList] extension, otherwise
 * you need to immediately dispatch the updates to the Adapter since [SimpleAdapterListDiffer]
 * updates its internal list as soon as it finishes processing current task.
 *
 * Moreover the default implementation of [SimpleAdapterListDiffer] uses Semaphore to disallow
 * overlaps. This is only beneficial when used correctly. Henceforth you need to implement a
 * non-cancelling queue.
 *
 * ```kotlin
 * val channel = Channel<List<MyData>>(Channel.RENDEZVOUS)
 *
 * // ... provide the channel to your data source
 *
 * channel.consumeAsFlow().collect {
 *     adapter.submitUpdate(it)
 * }
 * ```
 *
 * @see SimpleAdapterListDiffer
 * */
interface AdapterListDiffer<Data> {

    val currentList: List<Data>

    suspend fun doDiff(list: List<Data>): DiffUtil.DiffResult

}

fun <Data> AdapterListDiffer<Data>.getItemAt(position: Int): Data? =
    currentList.getOrNull(position)

val <Data> AdapterListDiffer<Data>.size
    inline get() = currentList.size

suspend fun <DifferAdapter, Data> DifferAdapter.submitList(list: List<Data>)
        where DifferAdapter : RecyclerView.Adapter<*>,
              DifferAdapter : AdapterListDiffer<Data> {
    val result = doDiff(list)
    withContext(Dispatchers.Main.immediate) {
        result.dispatchUpdatesTo(this@submitList)
    }
}
