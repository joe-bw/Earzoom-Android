package kr.co.sorizava.asrplayerKt.media

import android.util.Log
import kotlin.experimental.and
import kotlin.experimental.or


class Resampler {




    /*

	public byte[] reSample(byte[] sourceData, int length, int bitsPerSample, int sourceRate, int targetRate) {

		// make the bytes to amplitudes first
		int bytePerSample = bitsPerSample / 8;
		int numSamples = length / bytePerSample;
		short[] amplitudes = new short[numSamples];	// 16 bit, use a short to store

		int pointer = 0;
		for (int i = 0; i < numSamples; i++) {
			short amplitude = 0;
			for (int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) {
				// little endian
				amplitude |= (short) ((sourceData[pointer++] & 0xFF) << (byteNumber * 8));
			}
			amplitudes[i] = amplitude;
		}
		// end make the amplitudes

		// do interpolation
		LinearInterpolation reSample=new LinearInterpolation();
		short[] targetSample = reSample.interpolate(sourceRate, targetRate, amplitudes);
		int targetLength = targetSample.length;
		// end do interpolation

		// TODO: Remove the high frequency signals with a digital filter, leaving a signal containing only half-sample-rated frequency information, but still sampled at a rate of target sample rate. Usually FIR is used

		// end resample the amplitudes

		// convert the amplitude to bytes
		byte[] bytes;
		if (bytePerSample==1){
			bytes= new byte[targetLength];
			for (int i=0; i<targetLength; i++){
				bytes[i]=(byte)targetSample[i];
			}
		}
		else{
			// suppose bytePerSample==2
			bytes= new byte[targetLength*2];
			for (int i=0; i<targetSample.length; i++){
				// little endian
				bytes[i*2] = (byte)(targetSample[i] & 0xff);
				bytes[i*2+1] = (byte)((targetSample[i] >> 8) & 0xff);
			}
		}
		// end convert the amplitude to bytes

		return bytes;
	}
	*/


    var PrintCount :Int =0;
    fun reSample(
        sourceData: ByteArray,
        length: Int,
        bitsPerSample: Int,
        sourceRate: Int,
        targetRate: Int,
    ): ByteArray? {

        PrintCount++;
        // make the bytes to amplitudes first
        val bytePerSample = bitsPerSample / 8
        val numSamples = length / bytePerSample
        val amplitudes = ShortArray(numSamples) // 16 bit, use a short to store
        var pointer = 0
        for (i in 0 until numSamples) {
            var amplitude: Short = 0

/*
            if (sourceData[0] != 0.toByte()) Log.e("pointer:", pointer.toString())
            if (sourceData[0] != 0.toByte()) Log.e("sourceData[pointer]:",
                sourceData[pointer].toString())
            if (sourceData[0] != 0.toByte()) Log.e("sourceData[pointer+1]:",
                sourceData[pointer + 1].toString())
*/
            for (byteNumber in 0 until bytePerSample) {
                // little endian

/*
                if (sourceData[0] != 0.toByte()) Log.e("byteNumber=$byteNumber amplitude:", amplitude.toString())
                if (sourceData[0] != 0.toByte()) Log.e("byteNumber=$byteNumber sourceData[pointer]:", sourceData[pointer].toString())
                if (sourceData[0] != 0.toByte())
                {
                    Log.e("byteNumber=$byteNumber 0xff.toByte():", (sourceData[pointer].toUShort() and 0xff.toUShort()).toShort().toString())
                }
                if (sourceData[0] != 0.toByte()) Log.e("byteNumber=$byteNumber shl:", ((sourceData[pointer].toUShort() and 0xff.toUShort()).toShort().toInt() shl (byteNumber * 8)).toString())
                if (sourceData[0] != 0.toByte()) Log.e("byteNumber=$byteNumber shl.toShort():", ((sourceData[pointer].toUShort() and 0xff.toUShort()).toUShort().toUInt() shl (byteNumber * 8)).toString())
                if (sourceData[0] != 0.toByte()) Log.e("byteNumber=$byteNumber amplitude or shl.toShort():", (amplitude or((sourceData[pointer].toUShort() and 0xFF.toUShort()).toUInt() shl (byteNumber * 8)).toShort()).toString())
*/

                        // little endian

                amplitude = amplitude or((sourceData[pointer++].toUShort() and 0xFF.toUShort()).toUInt() shl (byteNumber * 8)).toShort()



//                if (sourceData[0] != 0.toByte()) Log.e("byteNumber=$byteNumber amplitude:", amplitude.toString())

            }

            amplitudes[i] = amplitude

//            if (sourceData[0] != 0.toByte()) Log.e("amplitudes[i=$i]:", amplitudes[i].toString())

        }

/*
        if (sourceData[0] != 0.toByte()) {
            val a = 1
        }
        // end make the amplitudes
        if (PrintCount % 5000 == 0) {
            Log.e("PrintCount:", PrintCount.toString())
            Log.e("sourceData:", sourceData.size.toString())
            var sourceDataStr = ""
            for (i in sourceData.indices) sourceDataStr += sourceData[i].toString()
            Log.e("sourceData:", sourceDataStr!!)

            Log.e("amplitudes:", amplitudes.size.toString())
            var amplitudesStr = ""
            for (i in amplitudes.indices) amplitudesStr += amplitudes[i].toString()
            Log.e("amplitudes:", amplitudesStr!!)
        }
*/

        // do interpolation
        val reSample = LinearInterpolation()
        val targetSample = reSample.interpolate(sourceRate, targetRate, amplitudes)
        val targetLength = targetSample!!.size
        // end do interpolation

        // TODO: Remove the high frequency signals with a digital filter, leaving a signal containing only half-sample-rated frequency information, but still sampled at a rate of target sample rate. Usually FIR is used

        // end resample the amplitudes

        // convert the amplitude to bytes
        val bytes: ByteArray
        if (bytePerSample == 1) {
            bytes = ByteArray(targetLength)
            for (i in 0 until targetLength) {
                bytes[i] = targetSample[i].toByte()
            }
        } else {
            // suppose bytePerSample==2
            bytes = ByteArray(targetLength * 2)
            for (i in targetSample.indices) {
                // little endian
                bytes[i * 2] = (targetSample[i] and 0xff).toByte()
                bytes[i * 2 + 1] = ( (targetSample[i].toInt() shr 8) and 0xff).toByte()
            }
        }
/*
        // end convert the amplitude to bytes
        if (PrintCount % 5000 == 0) {
            Log.e("bytes:", bytes.size.toString())
            var bytesStr = ""
            for (i in sourceData.indices) bytesStr += bytes[i].toString()
            Log.e("bytes:", bytesStr!!)

        }
*/
        // end convert the amplitude to bytes
        return bytes
    }
}