package com.example.playlistmaker.player.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var isPlaying = false
    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null
    private var currentDrawable: Drawable? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playDrawable = getDrawable(R.styleable.PlaybackButtonView_imagePlayId)
                pauseDrawable = getDrawable(R.styleable.PlaybackButtonView_imagePauseId)
                currentDrawable = playDrawable
            } finally {
                recycle()
            }
        }
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        playDrawable?.setBounds(0,0,w,h)
        pauseDrawable?.setBounds(0,0,w,h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        currentDrawable?.draw(canvas)
    }

    fun setPlaying(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            updateBitmap()
            invalidate()
        }
    }

    private fun updateBitmap() {
        currentDrawable = if (isPlaying) pauseDrawable else playDrawable
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { return true }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
