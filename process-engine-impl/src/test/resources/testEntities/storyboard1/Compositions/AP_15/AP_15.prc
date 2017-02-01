<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:process xmlns:ns2="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    <ns2:sequence>
        <ns2:invoke>
            <name>RegistrationRequest</name>
            <order>0</order>
        </ns2:invoke>
        <ns2:reply>
            <name>RegistrationAck</name>
            <order>1</order>
        </ns2:reply>
        <ns2:invoke>
            <name>FlexibusBookingRequest</name>
            <order>2</order>
        </ns2:invoke>
        <ns2:reply>
            <name>BookingResult</name>
            <order>3</order>
        </ns2:reply>
    </ns2:sequence>
</ns2:process>
