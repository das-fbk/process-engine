<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:process xmlns:ns2="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    <ns2:sequence>
        <ns2:invoke>
            <name>UMS_TripManagementRequest</name>
            <order>0</order>
        </ns2:invoke>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>UMS_concrete1</name>
            <order>1</order>
        </ns2:switch>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>UMS_concrete2</name>
            <order>2</order>
        </ns2:switch>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>UMS_concrete3</name>
            <order>3</order>
        </ns2:switch>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>UMS_concrete4</name>
            <order>4</order>
        </ns2:switch>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>UMS_concrete5</name>
            <order>5</order>
        </ns2:switch>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>UMS_ManagementTrip</name>
            <order>6</order>
        </ns2:switch>
        <ns2:reply>
            <name>UMS_TripManagementReply</name>
            <order>7</order>
        </ns2:reply>
    </ns2:sequence>
</ns2:process>
