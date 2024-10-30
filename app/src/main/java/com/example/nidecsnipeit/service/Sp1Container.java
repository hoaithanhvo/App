package com.example.nidecsnipeit.service;

import android.widget.Toast;

import com.densowave.scannersdk.Common.CommScanner;
import com.densowave.scannersdk.Common.CommStatusChangedEvent;
import com.densowave.scannersdk.Const.CommConst;
import com.densowave.scannersdk.Listener.ScannerStatusListener;

public class Sp1Container implements ScannerStatusListener {
    static Sp1Container container = null;
    public static CommScanner commScanner;
    public static Boolean scannerConnected = false;
    public static Sp1Container GetInstance()
    {
        if (container == null)
        {
            container = new Sp1Container();
        }
        return container;
    }

    public void SetConnectedScanner(CommScanner scanner)
    {
        if (scanner != null)
        {
            scannerConnected = true;
            scanner.addStatusListener(this);
        }
        else
        {
            scannerConnected = false;
            if (commScanner != null)
            {
                scanner.removeStatusListener(this);
            }
        }
        commScanner = scanner;
    }

    public CommScanner GetScanner()
    {
        return commScanner;
    }

    public Boolean IsScannerConnected()
    {
        return scannerConnected;
    }

    public void DisconnectScanner()
    {
        if (scannerConnected)
        {
            try{
                commScanner.close();
                commScanner.removeStatusListener(this);
                commScanner = null;
            }catch (Exception e){
            }

        }
    }

    @Override
    public void onScannerStatusChanged(CommScanner commScanner, CommStatusChangedEvent commStatusChangedEvent) {
        CommConst.ScannerStatus scannerStatus = commStatusChangedEvent.getStatus();
        if (commScanner == commScanner && scannerStatus.equals(CommConst.ScannerStatus.CLOSE_WAIT))
        {
            if (scannerConnected)
            {
                scannerConnected = false;
            }
        }
    }
}
