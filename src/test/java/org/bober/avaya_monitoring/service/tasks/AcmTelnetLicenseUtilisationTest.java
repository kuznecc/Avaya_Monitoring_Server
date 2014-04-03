package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 */
public class AcmTelnetLicenseUtilisationTest extends AbstractAcmTelnetTaskTest {

    private ArrayList<String> telnetOutputExample = new ArrayList<String>() {{
        add("display system-parameters customer-options`7`display system-parameters customer-options `8`Page 1 of 11`OPTIONAL FEATURES`G3 Version: `V15 `Software Package: `Enterprise `Location: `2`Platform: `6 `RFA System ID `SID`: `86443 `RFA Module ID `MID`: `1 `USED`Platform Maximum Ports: `44000`2518 `Maximum Stations: `1566 `1268 `Maximum XMOBILE Stations: `0 `0 `Maximum Off-PBX Telephones - EC500: `1355 `0 `Maximum Off-PBX Telephones - OPS: `1820 `6 `Maximum Off-PBX Telephones - PBFMC: `1350 `0 `Maximum Off-PBX Telephones - PVFMC: `0 `0 `Maximum Off-PBX Telephones - SCCAN: `0 `0 `NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 2 of 11`OPTIONAL FEATURES`IP PORT CAPACITIES`USED`Maximum Administered H.323 Trunks: `8000 `0 `Maximum Concurrently Registered IP Stations: `18000`987 `Maximum Administered Remote Office Trunks: `8000 `0 `Maximum Concurrently Registered Remote Office Stations: `18000`0 `Maximum Concurrently Registered IP eCons: `0 `0 `Max Concur Registered Unauthenticated H.323 Stations: `0 `0 `Maximum Video Capable H.323 Stations: `0 `0 `Maximum Video Capable IP Softphones: `0 `0 `Maximum Administered SIP Trunks: `2694 `1250 `Maximum Administered Ad-hoc Video Conferencing Ports: `0 `0 `Maximum Number of DS1 Boards with Echo Cancellation: `400`0 `Maximum TN2501 VAL Boards: `10 `1 ` Maximum Media Gateway VAL Sources: `250`0 `Maximum TN2602 Boards with 80 VoIP Channels: `128 `0 `Maximum TN2602 Boards with 320 VoIP Channels: `128 `8 `Maximum Number of Expanded Meet-me Conference Ports: `0 `0 `NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 3 of 11`OPTIONAL FEATURES`Abbreviated Dialing Enhanced List? `y`Access Security Gateway `ASG`? `n`Analog Trunk Incoming Call ID? `y`A/D Grp/Sys List Dialing Start at 01? `y`Answer Supervision by Call Classifier? `n`ARS? `y`ARS/AAR Partitioning? `y`ARS/AAR Dialing without FAC? `y`ASAI Link Core Capabilities? `n`ASAI Link Plus Capabilities? `n`Async. Transfer Mode `ATM` PNC? `n`Async. Transfer Mode `ATM` Trunking? `n`ATM WAN Spare Processor? `n`ATMS? `y`Attendant Vectoring? `y`Audible Message Waiting? `y`Authorization Codes? `y`CAS Branch? `n`CAS Main? `n`Change COR by FAC? `n`Computer Telephony Adjunct Links? `y`Cvg Of Calls Redirected Off-net? `y`DCS `Basic`? `y`DCS Call Coverage? `y`DCS with Rerouting? `y`Digital Loss Plan Modification? `y`DS1 MSP? `n`DS1 Echo Cancellation? `y`NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 4 of 11`OPTIONAL FEATURES`Emergency Access to Attendant? `y`Enable `dadmin` Login? `y`Enhanced Conferencing? `y`Enhanced EC500? `y`Enterprise Survivable Server? `n`Enterprise Wide Licensing? `n`ESS Administration? `n`Extended Cvg/Fwd Admin? `y`External Device Alarm Admin? `y`Five Port Networks Max Per MCC? `n`Flexible Billing? `n`Forced Entry of Account Codes? `y`Global Call Classification? `y`Hospitality `Basic`? `y`Hospitality `G3V3 Enhancements`? `y`IP Trunks? `y`IP Attendant Consoles? `y`IP Stations? `y`ISDN Feature Plus? `y`ISDN/SIP Network Call Redirection? `y`ISDN-BRI Trunks? `y`ISDN-PRI? `y`Local Survivable Processor? `n`Malicious Call Trace? `y`Media Encryption Over IP? `y`Mode Code for Centralized Voice Mail? `n`Multifrequency Signaling? `y`Multimedia Call Handling `Basic`? `y`Multimedia Call Handling `Enhanced`? `y`Multimedia IP SIP Trunking? `n`NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 5 of 11`OPTIONAL FEATURES`Multinational Locations? `n`Multiple Level Precedence & Preemption? `n`Multiple Locations? `y`Personal Station Access `PSA`? `y`PNC Duplication? `y`Port Network Support? `y`Posted Messages? `y`Private Networking? `y`Processor and System MSP? `n`Processor Ethernet? `y`Remote Office? `y`Restrict Call Forward Off Net? `y`Secondary Data Module? `y`Station and Trunk MSP? `n`Station as Virtual Extension? `y`System Management Data Transfer? `y`Tenant Partitioning? `y`Terminal Trans. Init. `TTI`? `y`Time of Day Routing? `y`TN2501 VAL Maximum Capacity? `y`Uniform Dialing Plan? `y`Usage Allocation Enhancements? `y`Wideband Switching? `y`Wireless? `y`NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 6 of 11`CALL CENTER OPTIONAL FEATURES`Call Center Release: `5.0 `ACD? `y`BCMS `Basic`? `y`BCMS/VuStats Service Level? `y`BSR Local Treatment for IP & ISDN? `y`Business Advocate? `n`Call Work Codes? `y`DTMF Feedback Signals For VRU? `y`Dynamic Advocate? `n`Expert Agent Selection `EAS`? `y`EAS-PHD? `y`Forced ACD Calls? `n`Least Occupied Agent? `y`Lookahead Interflow `LAI`? `y`Multiple Call Handling `On Request`? `y`Multiple Call Handling `Forced`? `y`PASTE `Display PBX Data on Phone`? `y`Reason Codes? `y`Service Level Maximizer? `y`Service Observing `Basic`? `y`Service Observing `Remote/By FAC`? `y`Service Observing `VDNs`? `y`Timed ACW? `y`Vectoring `Basic`? `y`Vectoring `Prompting`? `y`Vectoring `G3V4 Enhanced`? `y`Vectoring `3.0 Enhanced`? `y`Vectoring `ANI/II-Digits Routing`? `y`Vectoring `G3V4 Advanced Routing`? `y`Vectoring `CINFO`? `y`Vectoring `Best Service Routing`? `y`Vectoring `Holidays`? `y`Vectoring `Variables`? `y`NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 7 of 11`CALL CENTER OPTIONAL FEATURES`VDN of Origin Announcement? `y`VDN Return Destination? `y`VuStats? `y`VuStats `G3V4 Enhanced`? `y`USED`Logged-In ACD Agents: `416 `104 `Logged-In Advocate Agents: `0 `0 `Logged-In IP Softphone Agents: `416 `103 `Logged-In SIP EAS Agents: `416 `20 `NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 8 of 11`QSIG OPTIONAL FEATURES`Basic Call Setup? `y`Basic Supplementary Services? `y`Centralized Attendant? `y`Interworking with DCS? `y`Supplementary Services with Rerouting? `y`Transfer into QSIG Voice Mail? `y`Value-Added `VALU`? `y`NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 9 of 11`ASAI ENHANCED FEATURES`Increased Adjunct Route Capacity? `n`ASAI PROPRIETARY FEATURES`Agent States? `y`NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 10 of 11`MAXIMUM IP REGISTRATIONS BY PRODUCT ID`Product ID Rel. Limit`Used`AgentSC `: `416 `21 `IP_API_A `: `1316 `479 `IP_API_B `: `10 `0 `IP_Agent `7 `: `463 `103 `IP_Phone `: `18000`25 `IP_ROMax `: `18000`26 `IP_Soft `: `5 `0 `IP_Soft `5 `: `1350 `27 `oneX_Comm `: `18000`28 `: `0 `0 `: `0 `0 `: `0 `0 `: `0 `0 `: `0 `0 `: `0 `0 `NOTE: You must logoff & login to effect the permission changes.`");
    }};
    private ArrayList<CheckResult> checkResultsFromTelnetOutput = new ArrayList<CheckResult>() {{
        add(new CheckResult(new Date(1396285200000L), 17, 104));
        add(new CheckResult(new Date(1396285200000L), 19, 103));
        add(new CheckResult(new Date(1396285200000L), 20, 20));
        add(new CheckResult(new Date(1396285200000L), 21, 21));
        add(new CheckResult(new Date(1396285200000L), 24, 103));
        add(new CheckResult(new Date(1396285200000L), 25, 25));
        add(new CheckResult(new Date(1396285200000L), 26, 26));
        add(new CheckResult(new Date(1396285200000L), 27, 27));
        add(new CheckResult(new Date(1396285200000L), 28, 28));
    }};

    private ArrayList<String> telnetOutputPartiallyExample = new ArrayList<String>() {{
        add("`7`display system-parameters customer-options `8`Page 2 of 11`OPTIONAL FEATURES`IP PORT CAPACITIES`USED`Maximum Administered H.323 Trunks: `8000 `0 `Maximum Concurrently Registered IP Stations: `18000`987 `Maximum Administered Remote Office Trunks: `8000 `0 `Maximum Concurrently Registered Remote Office Stations: `18000`0 `Maximum Concurrently Registered IP eCons: `0 `0 `Max Concur Registered Unauthenticated H.323 Stations: `0 `0 `Maximum Video Capable H.323 Stations: `0 `0 `Maximum Video Capable IP Softphones: `0 `0 `Maximum Administered SIP Trunks: `2694 `1250 `Maximum Administered Ad-hoc Video Conferencing Ports: `0 `0 `Maximum Number of DS1 Boards with Echo Cancellation: `400`0 `Maximum TN2501 VAL Boards: `10 `1 ` Maximum Media Gateway VAL Sources: `250`0 `Maximum TN2602 Boards with 80 VoIP Channels: `128 `0 `Maximum TN2602 Boards with 320 VoIP Channels: `128 `8 `Maximum Number of Expanded Meet-me Conference Ports: `0 `0 `NOTE: You must logoff & login to effect the permission changes.`");
        add("`7`display system-parameters customer-options `8`Page 7 of 11`CALL CENTER OPTIONAL FEATURES`VDN of Origin Announcement? `y`VDN Return Destination? `y`VuStats? `y`VuStats `G3V4 Enhanced`? `y`USED`Logged-In ACD Agents: `416 `104 `Logged-In Advocate Agents: `0 `0 `Logged-In IP Softphone Agents: `416 `103 `Logged-In SIP EAS Agents: `416 `0 `NOTE: You must logoff & login to effect the permission changes.`");
    }};

    private final int EXPECTED_CHECK_RESULTS_COUNT = 9;

    @Test
    public void testGetCheckResultsFromTelnetOutput() throws Exception {
        final AcmTelnetLicenseUtilisation acmTelnetLicenseUtilisation =
                new AcmTelnetLicenseUtilisation() {{
                    checkConfig = checkConfigDummy;
                    setMonitoredEntity(monitoredEntityDummy);
                }};

        /* NULL telnet output test */

        List<CheckResult> checkResults = acmTelnetLicenseUtilisation.getCheckResultsFromTelnetOutput(null);
        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", EXPECTED_CHECK_RESULTS_COUNT, checkResults.size());

        assertEquals("Some result objects have NOT-null value statement",
                EXPECTED_CHECK_RESULTS_COUNT, getCountOfNullValues(checkResults));

        assertEquals("Some result objects have null date statement", 0, getCountOfNullDate(checkResults));

        /* Empty telnet output test */

        checkResults = acmTelnetLicenseUtilisation.getCheckResultsFromTelnetOutput(new ArrayList<String>());
        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", EXPECTED_CHECK_RESULTS_COUNT, checkResults.size());

        assertEquals("Some result objects have NOT-null value statement",
                EXPECTED_CHECK_RESULTS_COUNT, getCountOfNullValues(checkResults));

        assertEquals("Some result objects have null date statement", 0, getCountOfNullDate(checkResults));

        /* Partially telnet output test */

        checkResults = acmTelnetLicenseUtilisation.getCheckResultsFromTelnetOutput(telnetOutputPartiallyExample);
        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", EXPECTED_CHECK_RESULTS_COUNT, checkResults.size());

        assertEquals("Count of null values wrong.", 6, getCountOfNullValues(checkResults));

        assertEquals("Some result objects have null date statement", 0, getCountOfNullDate(checkResults));

        /* Normally telnet output test */

        checkResults = acmTelnetLicenseUtilisation.getCheckResultsFromTelnetOutput(telnetOutputExample);
        assertNotNull("Method response was null", checkResults);

        assertEquals("Wrong count of result objects.", EXPECTED_CHECK_RESULTS_COUNT, checkResults.size());

        // Set date to my dummy like in task checkResults instances
        for (CheckResult checkResult : checkResultsFromTelnetOutput) {
            checkResult.setDate( checkResults.get(0).getDate());
        }

        assertEquals("Wrong CheckResult objects in the list.", checkResultsFromTelnetOutput, checkResults);


        assertEquals("Some result objects have NOT-null value statement", 0, getCountOfNullValues(checkResults));

        assertEquals("Some result objects have null date statement", 0, getCountOfNullDate(checkResults));


    }
}
