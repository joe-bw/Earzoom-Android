package org.mozilla.focus.activity

import android.content.*
import android.net.*
import android.util.Log
import kr.co.sorizava.asrplayerKt.websocket.WsManager
import java.io.*
import java.nio.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

class AudioMonitorThread() : Thread()
{
    private val pool: ScheduledExecutorService
    private var serverSocket: LocalServerSocket? = null
    private val isRunning: AtomicBoolean = AtomicBoolean(false)


    init
    {
        pool = ScheduledThreadPoolExecutor(3, ThreadFactory { r ->
            val thread = Thread(r)
            thread.name = TAG
            thread
        })
    }

    private fun closeServerSocket()
    {
        Log.e(TAG, "closeServerSocket")
        if (serverSocket != null)
        {
            try
            {
                Log.e(TAG, "serverSocket!!.close()")
                serverSocket!!.close()
            }
            catch (e: Exception)
            {
                // ignore
                Log.e(TAG, "serverSocket!!.close()  error")
            }

            serverSocket = null
        }
    }

    fun stopThread()
    {
        Log.d(TAG, "stopThread")
        isRunning.set(false)
    }

    override fun run()
    {
        Log.e(TAG, "run()")

        isRunning.set(true)

        if (!initServerSocket())
        {
            isRunning.set(false)
            return
        }

        val socket = serverSocket!!.accept()

        while (isRunning.get())
        {
            try
            {
                // handle socket
                handleLocalSocket(socket)
            }
            catch (e: Exception)
            {
                Log.e(TAG, "Error when accept socket", e)

                initServerSocket()
            }

        }

        // close inputStream
        try
        {
            socket.inputStream.close()
        }
        catch (e: IOException)
        {
            Log.e(TAG, "Error socket.inputStream.close() ", e)
        }

        // close socket
        try
        {
            socket.close()
        }
        catch (e: IOException)
        {
            Log.e(TAG, "Error socket.close() ", e)
        }


        closeServerSocket()
    }

    /**
     * handle local socket
     *
     * @param socket local socket object
     */
    private fun handleLocalSocket(socket: LocalSocket)
    {
      //  pool.execute {
            val input: InputStream
            try
            {
                input = socket.inputStream
               // input.read(pcm_buffer)
                val count = input.read(pcm_buffer)

                //Log.e(TAG, "##writeBuffer: " + count)
                WsManager.getInstance()?.writeBuffer(pcm_buffer, count)
            }
            catch (e: Exception)
            {
            }
            finally
            {
            }
      //  }
    }

    /**
     * init server socket
     *
     * @return init failed return false.
     */
    private fun initServerSocket(): Boolean
    {
        Log.e(TAG, "initServerSocket")
        // if not running, do not init
        if (!isRunning.get())
        {
            return false
        }

        return try
        {
            serverSocket = LocalServerSocket(SOCKET_NAME)
            Log.e(TAG, "initServerSocket OK")
            true
        }
        catch (e: IOException)
        {
            Log.e(TAG,"unable to bind", e)
            false
        }

    }

    companion object
    {
        private const val TAG = "AudioMonitorThread"
        private const val SOCKET_NAME = "kr.co.sorizava.pcmsocket"
        private val pcm_buffer = ByteArray(1920)
    }
}