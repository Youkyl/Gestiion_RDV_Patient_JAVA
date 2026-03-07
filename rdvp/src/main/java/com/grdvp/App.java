package com.grdvp;

import com.grdvp.factory.ObjectFactory;
import com.grdvp.view.PatientView;


public class App 
{
    public static void main( String[] args )
    {
        PatientView patientView = ObjectFactory.createPatientView();
        patientView.mainMenu();
    }
}
