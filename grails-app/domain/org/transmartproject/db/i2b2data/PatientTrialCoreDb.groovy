package org.transmartproject.db.i2b2data

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = 'patient,study')
class PatientTrialCoreDb implements Serializable {

    PatientDimension patient
    String study

    // unused
    //String secureObjToken

    static mapping = {
        table   name: 'patient_trial', schema: 'i2b2demodata'

        id      composite: ['patient', 'study']

        patient column: 'patient_num'
        study   column: 'trial',      index: 'trial_idx', unique: false

        version false
    }
}
