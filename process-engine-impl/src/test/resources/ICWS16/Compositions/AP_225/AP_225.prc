<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:process xmlns:ns2="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    <ns2:sequence>
        <ns2:invoke>
            <name>PP_PaymentRequest</name>
            <order>0</order>
        </ns2:invoke>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PP_concrete1</name>
            <order>1</order>
        </ns2:switch>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PP_concrete2</name>
            <order>2</order>
        </ns2:switch>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PP_concrete3</name>
            <order>3</order>
        </ns2:switch>
        <ns2:reply>
            <name>PP_PaymentReply</name>
            <order>4</order>
        </ns2:reply>
    </ns2:sequence>
</ns2:process>
