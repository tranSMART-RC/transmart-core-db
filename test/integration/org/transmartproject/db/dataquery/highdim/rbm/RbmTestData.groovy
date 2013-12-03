package org.transmartproject.db.dataquery.highdim.rbm

import org.transmartproject.db.biomarker.BioMarkerCoreDb
import org.transmartproject.db.dataquery.highdim.DeGplInfo
import org.transmartproject.db.dataquery.highdim.DeSubjectSampleMapping
import org.transmartproject.db.dataquery.highdim.HighDimTestData
import org.transmartproject.db.dataquery.highdim.SampleBioMarkerTestData
import org.transmartproject.db.i2b2data.PatientDimension
import org.transmartproject.db.search.SearchKeywordCoreDb

import static org.transmartproject.db.dataquery.highdim.HighDimTestData.save

class RbmTestData {

    public static final String TRIAL_NAME = 'RBM_SAMP_TRIAL'

    SampleBioMarkerTestData bioMarkerTestData = new SampleBioMarkerTestData()

    DeGplInfo platform = {
        def res = new DeGplInfo(
                title: 'RBM platform',
                organism: 'Homo Sapiens',
                markerTypeId: 'RBM')
        res.id = 'BOGUSRBMplatform'
        res
    }()

    List<PatientDimension> patients =
        HighDimTestData.createTestPatients(2, -300, TRIAL_NAME)

    List<DeSubjectSampleMapping> assays =
        HighDimTestData.createTestAssays(patients, -400, platform, TRIAL_NAME)

    List<DeRbmAnnotation> deRbmAnnotations = {
        def createAnnotation = { id, antigene, uniprotId, geneSymbol, geneId ->
            def res = new DeRbmAnnotation(
                    gplId: platform.id,
                    antigenName: antigene,
                    uniprotId: uniprotId,
                    geneSymbol: geneSymbol,
                    geneId: geneId
            )
            res.id = id
            res
        }
        [
                //Adiponectin
                createAnnotation(-501, 'Antigene1', 'Q15848', 'AURKA', -601),
                //Urea transporter 2
                createAnnotation(-502, 'Antigene2', 'Q15849', 'SLC14A2', -602),
                //Adipogenesis regulatory factor
                createAnnotation(-503, 'Antigene3', 'Q15847', 'ADIRF', -603),
        ]
    }()

    List<DeSubjectRbmData> rbmData = {
        def createRbmEntry = { DeSubjectSampleMapping assay,
                                 DeRbmAnnotation deRbmAnnotation,
                                      double intensity ->
            new DeSubjectRbmData(
                    trialName: TRIAL_NAME,
                    assay: assay,

                    annotation: deRbmAnnotation,
                    patient: assay.patient,

                    logIntensity: Math.log(intensity) / Math.log(2),
                    zscore: intensity * 2,
            )
        }

        def res = []
        Double intensity = 0
        deRbmAnnotations.each { deRbmAnnotation ->
            assays.each { assay ->
                res += createRbmEntry assay, deRbmAnnotation, (intensity += 0.1)
            }
        }

        res
    }()

    List<BioMarkerCoreDb> getBioMarkers() {
        bioMarkerTestData.geneBioMarkers
    }

    List<SearchKeywordCoreDb> searchKeywords = {
        bioMarkerTestData.geneSearchKeywords +
                bioMarkerTestData.proteinSearchKeywords +
                bioMarkerTestData.geneSignatureSearchKeywords
    }()

    void saveAll() {
        bioMarkerTestData.saveRbmData()

        save([ platform ])
        save patients
        save assays
        save deRbmAnnotations
        save rbmData
    }

}
