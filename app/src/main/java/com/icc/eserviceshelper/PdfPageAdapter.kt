package com.icc.eserviceshelper

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class PdfPageAdapter(private val pdfRenderer: PdfRenderer) :
    RecyclerView.Adapter<PdfPageAdapter.PageViewHolder>() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_page, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = pdfRenderer.pageCount

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        scope.cancel()
    }

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.pageImageView)
        private var job: Job? = null

        fun bind(pageIndex: Int) {
            job?.cancel()
            imageView.setImageBitmap(null) // Clear old bitmap while loading

            job = scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    val page = synchronized(pdfRenderer) {
                        pdfRenderer.openPage(pageIndex)
                    }
                    
                    val width = itemView.context.resources.displayMetrics.widthPixels
                    val height = (width.toFloat() / page.width * page.height).toInt()
                    
                    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    synchronized(pdfRenderer) {
                        page.render(b, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()
                    }
                    b
                }
                imageView.setImageBitmap(bitmap)
            }
        }
    }
}
