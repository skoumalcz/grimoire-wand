package com.skoumal.grimoire.wand.recyclerview.diff

import androidx.recyclerview.widget.DiffUtil
import com.skoumal.grimoire.wand.recyclerview.RecyclerViewDispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SimpleAdapterListDiffer<Data : Any>(
    private val differ: DiffUtil.ItemCallback<Data>,
    private val context: CoroutineContext = RecyclerViewDispatchers.Single
) : AdapterListDiffer<Data> {

    private val semaphore = Semaphore(1)

    override var currentList: List<Data> = emptyList()
        private set

    override suspend fun submitInternal(list: List<Data>): DiffUtil.DiffResult {
        semaphore.acquire()
        return withContext(context) {
            consumeList(list)
        }.also {
            currentList = list
            semaphore.release()
        }
    }

    private suspend fun consumeList(list: List<Data>) = coroutineScope {
        val callback = SimpleDiffUtilCallback(
            oldList = currentList,
            newList = list,
            differ = differ
        )

        DiffUtil.calculateDiff(callback, list.isNotEmpty() && currentList.isNotEmpty())
    }

}