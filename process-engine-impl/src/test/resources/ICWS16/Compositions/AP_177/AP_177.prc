<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:process xmlns:ns2="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    <ns2:sequence>
        <ns2:invoke>
            <name>SMC_MobilityCardRequest</name>
            <order>0</order>
        </ns2:invoke>
        <ns2:switch xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>SMC_SubscribeAndPay</name>
            <order>1</order>
        </ns2:switch>
        <ns2:reply>
            <name>SMC_MobilityCardReply</name>
            <order>2</order>
        </ns2:reply>
        <ns2:invoke>
            <name>SMC_ActivateServiceRequest</name>
            <order>3</order>
        </ns2:invoke>
        <ns2:reply>
            <name>SMC_ActivateServiceReply</name>
            <order>4</order>
        </ns2:reply>
    </ns2:sequence>
</ns2:process>
