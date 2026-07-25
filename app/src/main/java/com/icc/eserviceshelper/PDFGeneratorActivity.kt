package com.icc.eserviceshelper

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.icc.eserviceshelper.databinding.ActivityPdfGeneratorBinding
import com.icc.eserviceshelper.databinding.ItemPdfDocBinding
import com.icc.eserviceshelper.databinding.ItemPdfFaqBinding
import com.icc.eserviceshelper.databinding.ItemPdfStepBinding
import com.icc.eserviceshelper.databinding.ItemPdfTipBinding
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.LineSeparator
import java.io.File
import java.io.FileOutputStream

class PDFGeneratorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfGeneratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAccordions()
        setupDynamicLists()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "PDF Generator"
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_download -> {
                    downloadPdf()
                    true
                }
                R.id.action_import_json -> {
                    showImportJsonDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showImportJsonDialog() {
        val editText = EditText(this).apply {
            hint = "Paste JSON here..."
            minLines = 8
            gravity = android.view.Gravity.TOP
        }

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val margin = (16 * resources.displayMetrics.density).toInt()
        params.setMargins(margin, margin / 2, margin, margin / 2)
        editText.layoutParams = params
        container.addView(editText)

        MaterialAlertDialogBuilder(this)
            .setTitle("Import JSON Template")
            .setView(container)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Import") { _, _ ->
                val json = editText.text.toString().trim()
                if (json.isNotEmpty()) {
                    importJson(json)
                }
            }
            .show()
    }

    private fun importJson(json: String) {
        try {
            val data = Gson().fromJson(json, PdfData::class.java) ?: return

            // Clear dynamic lists
            binding.containerDocs.removeAllViews()
            binding.containerSteps.removeAllViews()
            binding.containerTips.removeAllViews()
            binding.containerFaq.removeAllViews()

            // Populate Static Fields
            binding.etPdfTitle.setText(data.title)
            binding.etPdfSubtitle.setText(data.subtitle)
            binding.etButtonText.setText(data.buttonText)
            binding.etButtonUrl.setText(data.buttonUrl)
            binding.etIntroContent.setText(data.intro)
            binding.etFooterTitle.setText(data.footerTitle)
            binding.etFooterSubtitle.setText(data.footerSubtitle)

            // Populate Dynamic Fields
            data.docs.forEach { doc ->
                val itemBinding = ItemPdfDocBinding.inflate(layoutInflater, binding.containerDocs, false)
                itemBinding.etDocName.setText(doc)
                itemBinding.btnRemoveDoc.setOnClickListener { binding.containerDocs.removeView(itemBinding.root) }
                binding.containerDocs.addView(itemBinding.root)
            }

            data.steps.forEach { step ->
                val itemBinding = ItemPdfStepBinding.inflate(layoutInflater, binding.containerSteps, false)
                itemBinding.etStepText.setText(step)
                itemBinding.btnRemoveStep.setOnClickListener { binding.containerSteps.removeView(itemBinding.root) }
                binding.containerSteps.addView(itemBinding.root)
            }

            data.tips.forEach { tip ->
                val itemBinding = ItemPdfTipBinding.inflate(layoutInflater, binding.containerTips, false)
                itemBinding.etTipTitle.setText(tip.title)
                itemBinding.etTipDescription.setText(tip.description)
                itemBinding.btnRemoveTip.setOnClickListener { binding.containerTips.removeView(itemBinding.root) }
                binding.containerTips.addView(itemBinding.root)
            }

            data.faqs.forEach { faq ->
                val itemBinding = ItemPdfFaqBinding.inflate(layoutInflater, binding.containerFaq, false)
                itemBinding.etQuestion.setText(faq.question)
                itemBinding.etAnswer.setText(faq.answer)
                itemBinding.btnRemoveFaq.setOnClickListener { binding.containerFaq.removeView(itemBinding.root) }
                binding.containerFaq.addView(itemBinding.root)
            }

            // Refresh UI state - Ensure sections are visible if they have content
            if (data.intro.isNotEmpty()) {
                binding.contentIntro.visibility = View.VISIBLE
                binding.iconIntro.rotation = 180f
            }
            if (data.docs.isNotEmpty()) {
                binding.contentDocs.visibility = View.VISIBLE
                binding.iconDocs.rotation = 180f
            }
            if (data.steps.isNotEmpty()) {
                binding.contentSteps.visibility = View.VISIBLE
                binding.iconSteps.rotation = 180f
            }
            if (data.tips.isNotEmpty()) {
                binding.contentTips.visibility = View.VISIBLE
                binding.iconTips.rotation = 180f
            }
            if (data.faqs.isNotEmpty()) {
                binding.contentFaq.visibility = View.VISIBLE
                binding.iconFaq.rotation = 180f
            }
            if (data.footerTitle.isNotEmpty()) {
                binding.contentFooter.visibility = View.VISIBLE
                binding.iconFooter.rotation = 180f
            }

            Toast.makeText(this, "Data imported successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage("Invalid JSON format.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun downloadPdf() {
        val title = binding.etPdfTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a PDF Title", Toast.LENGTH_SHORT).show()
            binding.contentBasicInfo.visibility = View.VISIBLE
            binding.etPdfTitle.requestFocus()
            return
        }

        val data = collectData()
        generatePdfFile(data)
    }

    private fun setupAccordions() {
        binding.headerBasicInfo.setOnClickListener { toggleSection(binding.contentBasicInfo, binding.iconBasicInfo) }
        binding.headerIntro.setOnClickListener { toggleSection(binding.contentIntro, binding.iconIntro) }
        binding.headerDocs.setOnClickListener { toggleSection(binding.contentDocs, binding.iconDocs) }
        binding.headerSteps.setOnClickListener { toggleSection(binding.contentSteps, binding.iconSteps) }
        binding.headerTips.setOnClickListener { toggleSection(binding.contentTips, binding.iconTips) }
        binding.headerFaq.setOnClickListener { toggleSection(binding.contentFaq, binding.iconFaq) }
        binding.headerFooter.setOnClickListener { toggleSection(binding.contentFooter, binding.iconFooter) }
    }

    private fun toggleSection(content: View, icon: View) {
        if (content.visibility == View.VISIBLE) {
            content.visibility = View.GONE
            icon.animate().rotation(0f).start()
        } else {
            content.visibility = View.VISIBLE
            icon.animate().rotation(180f).start()
        }
    }

    private fun setupDynamicLists() {
        binding.btnAddDoc.setOnClickListener { addDocField() }
        binding.btnAddStep.setOnClickListener { addStepField() }
        binding.btnAddTip.setOnClickListener { addTipField() }
        binding.btnAddFaq.setOnClickListener { addFaqField() }
    }

    private fun addDocField() {
        val itemBinding = ItemPdfDocBinding.inflate(layoutInflater, binding.containerDocs, false)
        itemBinding.btnRemoveDoc.setOnClickListener { binding.containerDocs.removeView(itemBinding.root) }
        binding.containerDocs.addView(itemBinding.root)
    }

    private fun addStepField() {
        val itemBinding = ItemPdfStepBinding.inflate(layoutInflater, binding.containerSteps, false)
        itemBinding.btnRemoveStep.setOnClickListener { binding.containerSteps.removeView(itemBinding.root) }
        binding.containerSteps.addView(itemBinding.root)
    }

    private fun addTipField() {
        val itemBinding = ItemPdfTipBinding.inflate(layoutInflater, binding.containerTips, false)
        itemBinding.btnRemoveTip.setOnClickListener { binding.containerTips.removeView(itemBinding.root) }
        binding.containerTips.addView(itemBinding.root)
    }

    private fun addFaqField() {
        val itemBinding = ItemPdfFaqBinding.inflate(layoutInflater, binding.containerFaq, false)
        itemBinding.btnRemoveFaq.setOnClickListener { binding.containerFaq.removeView(itemBinding.root) }
        binding.containerFaq.addView(itemBinding.root)
    }

    private fun collectData(): PdfData {
        val docs = mutableListOf<String>()
        for (i in 0 until binding.containerDocs.childCount) {
            val view = binding.containerDocs.getChildAt(i)
            val docBinding = ItemPdfDocBinding.bind(view)
            docs.add(docBinding.etDocName.text.toString().trim())
        }

        val steps = mutableListOf<String>()
        for (i in 0 until binding.containerSteps.childCount) {
            val view = binding.containerSteps.getChildAt(i)
            val stepBinding = ItemPdfStepBinding.bind(view)
            steps.add(stepBinding.etStepText.text.toString().trim())
        }

        val tips = mutableListOf<Tip>()
        for (i in 0 until binding.containerTips.childCount) {
            val view = binding.containerTips.getChildAt(i)
            val tipBinding = ItemPdfTipBinding.bind(view)
            tips.add(Tip(tipBinding.etTipTitle.text.toString().trim(), tipBinding.etTipDescription.text.toString().trim()))
        }

        val faqs = mutableListOf<Faq>()
        for (i in 0 until binding.containerFaq.childCount) {
            val view = binding.containerFaq.getChildAt(i)
            val faqBinding = ItemPdfFaqBinding.bind(view)
            faqs.add(Faq(faqBinding.etQuestion.text.toString().trim(), faqBinding.etAnswer.text.toString().trim()))
        }

        return PdfData(
            title = binding.etPdfTitle.text.toString().trim(),
            subtitle = binding.etPdfSubtitle.text.toString().trim(),
            buttonText = binding.etButtonText.text.toString().trim(),
            buttonUrl = binding.etButtonUrl.text.toString().trim(),
            intro = binding.etIntroContent.text.toString().trim(),
            docs = docs.filter { it.isNotEmpty() },
            steps = steps.filter { it.isNotEmpty() },
            tips = tips.filter { it.title.isNotEmpty() || it.description.isNotEmpty() },
            faqs = faqs.filter { it.question.isNotEmpty() || it.answer.isNotEmpty() },
            footerTitle = binding.etFooterTitle.text.toString().trim(),
            footerSubtitle = binding.etFooterSubtitle.text.toString().trim(),
        )
    }

    private fun generatePdfFile(data: PdfData): File? {
        val cleanTitle = data.title.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        val fileName = "$cleanTitle.pdf"
        val tempFile = File(getExternalFilesDir(null), fileName)

        // Set A4 page size with proper margins for professional typesetting
        val document = Document(PageSize.A4, 45f, 45f, 50f, 85f)
        
        try {
            val writer = PdfWriter.getInstance(document, FileOutputStream(tempFile))
            
            // Refined Color Palette to match the image exactly
            val colorPrimaryBlue = BaseColor(13, 71, 161) // Deep Blue
            val colorLightBlueBg = BaseColor(241, 248, 254) // Very Light Blue
            val colorGreyText = BaseColor(97, 97, 97)
            val colorTipBg = BaseColor(255, 253, 231) // Light Yellow
            val colorTipStrip = BaseColor(255, 179, 0) // Orange
            val colorDivider = BaseColor(224, 224, 224) // Light Grey
            
            val baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED)
            val fontTitle = Font(baseFont, 22f, Font.BOLD, colorPrimaryBlue)
            val fontSubtitle = Font(baseFont, 12f, Font.NORMAL, colorGreyText)
            val fontSectionHeader = Font(baseFont, 14f, Font.BOLD, colorPrimaryBlue)
            val fontBold = Font(baseFont, 11f, Font.BOLD, BaseColor.BLACK)
            val fontNormal = Font(baseFont, 11f, Font.NORMAL, BaseColor.BLACK)
            val fontFooterTitle = Font(baseFont, 10f, Font.BOLD, colorGreyText)
            val fontFooterSub = Font(baseFont, 9f, Font.NORMAL, colorGreyText)
            val fontButton = Font(baseFont, 12f, Font.BOLD, BaseColor.WHITE)

            document.open()

            // 1. HEADER (Centered Title & Subtitle)
            val pTitle = Paragraph(data.title.uppercase(), fontTitle)
            pTitle.alignment = Element.ALIGN_CENTER
            pTitle.spacingBefore = 5f
            document.add(pTitle)

            if (data.subtitle.isNotEmpty()) {
                val pSub = Paragraph(data.subtitle, fontSubtitle)
                pSub.alignment = Element.ALIGN_CENTER
                pSub.spacingAfter = 10f
                document.add(pSub)
            }

            // Thick Blue Divider Header
            val line = LineSeparator(2f, 100f, colorPrimaryBlue, Element.ALIGN_CENTER, 0f)
            val linePara = Paragraph()
            linePara.add(line)
            linePara.spacingAfter = 25f
            document.add(linePara)

            // 2. APPLY BUTTON (Centered with Rounded Corners)
            if (data.buttonText.isNotEmpty()) {
                val tableBtn = PdfPTable(1)
                tableBtn.totalWidth = 280f
                tableBtn.isLockedWidth = true
                tableBtn.horizontalAlignment = Element.ALIGN_CENTER
                
                val cellBtn = PdfPCell()
                cellBtn.border = Rectangle.NO_BORDER
                cellBtn.setPadding(12f)
                cellBtn.backgroundColor = colorPrimaryBlue
                
                cellBtn.cellEvent = PdfPCellEvent { _, position, canvases ->
                    val cb = canvases[PdfPTable.BACKGROUNDCANVAS]
                    cb.roundRectangle(position.left, position.bottom, position.width, position.height, 5f)
                    cb.setColorFill(colorPrimaryBlue)
                    cb.fill()
                    
                    if (data.buttonUrl.isNotEmpty() && data.buttonUrl.startsWith("http")) {
                        val action = PdfAction(data.buttonUrl)
                        val annotation = PdfAnnotation.createLink(writer, position, PdfAnnotation.HIGHLIGHT_INVERT, action)
                        writer.addAnnotation(annotation)
                    }
                }
                
                val pBtn = Paragraph(data.buttonText.uppercase(), fontButton)
                pBtn.alignment = Element.ALIGN_CENTER
                cellBtn.addElement(pBtn)
                
                tableBtn.addCell(cellBtn)
                tableBtn.spacingAfter = 35f
                document.add(tableBtn)
            }

            // Universal Section Add Helper (Modified to allow natural page flow and vertical centering)
            fun addSection(title: String, contentElements: List<Element>, spacingBefore: Float = 15f) {
                val sectionTable = PdfPTable(1)
                sectionTable.widthPercentage = 100f
                sectionTable.spacingBefore = spacingBefore
                sectionTable.spacingAfter = 15f
                
                // Allow the table and its rows to split across pages naturally
                sectionTable.keepTogether = false
                
                // Header Row
                val headerCell = PdfPCell()
                headerCell.backgroundColor = colorLightBlueBg
                headerCell.border = Rectangle.NO_BORDER
                
                // Vertical centering logic
                headerCell.verticalAlignment = Element.ALIGN_MIDDLE
                headerCell.setUseAscender(true)
                headerCell.setUseDescender(true)
                
                headerCell.paddingTop = 11f
                headerCell.paddingBottom = 11f
                headerCell.paddingLeft = 20f
                
                headerCell.cellEvent = PdfPCellEvent { _, position, canvases ->
                    val cb = canvases[PdfPTable.LINECANVAS]
                    cb.setColorFill(colorPrimaryBlue)
                    cb.rectangle(position.left, position.bottom, 4f, position.height)
                    cb.fill()
                }
                
                // Using setPhrase for the most accurate vertical alignment
                headerCell.phrase = Phrase(title, fontSectionHeader)
                sectionTable.addCell(headerCell)
                
                // Add content elements as separate rows to ensure they can split across pages
                contentElements.forEach { element ->
                    val contentCell = PdfPCell()
                    contentCell.border = Rectangle.NO_BORDER
                    contentCell.paddingTop = 5f
                    contentCell.paddingBottom = 5f
                    contentCell.paddingLeft = 5f
                    contentCell.addElement(element)
                    sectionTable.addCell(contentCell)
                }
                
                document.add(sectionTable)
            }

            // 3. INTRODUCTION
            if (data.intro.isNotEmpty()) {
                val pIntro = Paragraph(data.intro, fontNormal)
                pIntro.leading = 16f
                pIntro.alignment = Element.ALIGN_JUSTIFIED
                addSection("1. Introduction", listOf(pIntro))
            }

            // 4. DOCUMENTS REQUIRED
            if (data.docs.isNotEmpty()) {
                val list = com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED)
                list.setListSymbol(Chunk("  • ", fontBold))
                list.symbolIndent = 20f
                data.docs.forEach { doc ->
                    val p = Paragraph()
                    if (doc.contains(":")) {
                        val parts = doc.split(":", limit = 2)
                        p.add(Chunk(parts[0].trim() + ": ", fontBold))
                        p.add(Chunk(parts[1].trim(), fontNormal))
                    } else {
                        p.add(Chunk(doc, fontNormal))
                    }
                    val listItem = ListItem(p)
                    listItem.spacingAfter = 6f
                    list.add(listItem)
                }
                addSection("2. Documents Required", listOf(list))
            }

            // 5. STEP-BY-STEP (Each step as a separate item for better page flow)
            if (data.steps.isNotEmpty()) {
                val stepsContent = data.steps.mapIndexed { index, step ->
                    val table = PdfPTable(1)
                    table.widthPercentage = 100f
                    val cell = PdfPCell()
                    cell.border = Rectangle.NO_BORDER
                    
                    // Vertical centering logic for steps
                    cell.verticalAlignment = Element.ALIGN_MIDDLE
                    cell.setUseAscender(true)
                    cell.setUseDescender(true)
                    
                    cell.paddingTop = 10f
                    cell.paddingBottom = 10f
                    cell.paddingLeft = 20f
                    
                    // Simple left accent for each step
                    cell.cellEvent = PdfPCellEvent { _, position, canvases ->
                        val cb = canvases[PdfPTable.LINECANVAS]
                        cb.setColorFill(colorPrimaryBlue)
                        cb.rectangle(position.left, position.bottom, 2f, position.height)
                        cb.fill()
                    }
                    
                    val p = Paragraph("${index + 1}. $step", fontNormal)
                    p.leading = 16f
                    cell.addElement(p)
                    table.addCell(cell)
                    table
                }
                addSection("3. Step-by-Step Instructions", stepsContent)
            }

            // 6. IMPORTANT TIPS (Yellow Card with Orange Strip)
            if (data.tips.isNotEmpty()) {
                val tipsContent = data.tips.map { tip ->
                    val t = PdfPTable(1)
                    t.widthPercentage = 100f
                    t.spacingBefore = 8f // Small gap between multiple tips
                    val c = PdfPCell()
                    c.border = Rectangle.NO_BORDER
                    
                    // Vertical centering for tips text
                    c.verticalAlignment = Element.ALIGN_MIDDLE
                    c.setUseAscender(true)
                    c.setUseDescender(true)
                    
                    c.setPadding(15f)
                    c.paddingLeft = 20f
                    c.backgroundColor = colorTipBg
                    c.cellEvent = PdfPCellEvent { _, position, canvases ->
                        val cb = canvases[PdfPTable.LINECANVAS]
                        cb.setColorFill(colorTipStrip)
                        cb.rectangle(position.left, position.bottom, 4f, position.height)
                        cb.fill()
                        cb.roundRectangle(position.left, position.bottom, position.width, position.height, 6f)
                        cb.setColorStroke(colorDivider)
                        cb.setLineWidth(0.5f)
                        cb.stroke()
                    }
                    val tp = Paragraph()
                    tp.add(Chunk("${tip.title}: ", fontBold))
                    tp.add(Chunk(tip.description, fontNormal))
                    tp.leading = 16f
                    c.addElement(tp)
                    t.addCell(c)
                    t
                }
                addSection("4. Important Tips", tipsContent, spacingBefore = 25f) // spacingBefore adds gap from header
            }

            // 7. FAQ (Questions & Answers)
            if (data.faqs.isNotEmpty()) {
                val faqContent = mutableListOf<Element>()
                data.faqs.forEach { faq ->
                    val q = Paragraph("Q: ${faq.question}", fontBold)
                    q.spacingAfter = 4f
                    faqContent.add(q)
                    val a = Paragraph("Ans: ${faq.answer}", fontNormal)
                    a.leading = 16f
                    a.spacingAfter = 15f
                    faqContent.add(a)
                }
                addSection("5. Frequently Asked Questions", faqContent)
            }

            // 8. FOOTER (Bottom of last page)
            if (data.footerTitle.isNotEmpty()) {
                val cb = writer.directContent
                val pageSize = document.pageSize
                
                // Grey divider line above footer
                cb.setColorStroke(colorDivider)
                cb.setLineWidth(1f)
                cb.moveTo(document.leftMargin(), 85f)
                cb.lineTo(pageSize.width - document.rightMargin(), 85f)
                cb.stroke()
                
                val ct = ColumnText(cb)
                ct.setSimpleColumn(document.leftMargin(), 20f, pageSize.width - document.rightMargin(), 80f)
                
                val fP = Paragraph(data.footerTitle, fontFooterTitle)
                fP.alignment = Element.ALIGN_CENTER
                if (data.footerSubtitle.isNotEmpty()) {
                    fP.add(Chunk.NEWLINE)
                    fP.add(Phrase(data.footerSubtitle, fontFooterSub))
                }
                ct.addElement(fP)
                ct.go()
            }

            document.close()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { output ->
                        tempFile.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                    Toast.makeText(this, "PDF Downloaded: $fileName", Toast.LENGTH_LONG).show()
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val finalFile = File(downloadsDir, fileName)
                tempFile.copyTo(finalFile, overwrite = true)
                Toast.makeText(this, "PDF Downloaded to Downloads folder", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    data class PdfData(
        val title: String,
        val subtitle: String,
        val buttonText: String,
        val buttonUrl: String,
        val intro: String,
        val docs: List<String>,
        val steps: List<String>,
        val tips: List<Tip>,
        val faqs: List<Faq>,
        val footerTitle: String,
        val footerSubtitle: String,
    )

    data class Tip(val title: String, val description: String)
    data class Faq(val question: String, val answer: String)
}
