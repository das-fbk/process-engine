<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:process xmlns:ns2="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    <ns2:sequence>
        <ns2:invoke>
            <name>RP_ConfirmationRequest</name>
            <order>0</order>
        </ns2:invoke>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>RP_PayTicket</name>
            <order>1</order>
        </ns2:reply>
        <ns2:reply>
            <name>RP_ConfirmationStatus</name>
            <order>2</order>
        </ns2:reply>
    </ns2:sequence>
</ns2:process>
