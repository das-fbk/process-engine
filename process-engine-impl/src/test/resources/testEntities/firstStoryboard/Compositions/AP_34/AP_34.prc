<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:process xmlns:ns2="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    <ns2:sequence>
        <ns2:invoke>
            <name>FC_UserRegistrationRequest</name>
            <order>0</order>
        </ns2:invoke>
        <ns2:reply>
            <name>FC_UserRegistrationAck</name>
            <order>1</order>
        </ns2:reply>
        <ns2:invoke>
            <name>FC_UserLoginRequest</name>
            <order>2</order>
        </ns2:invoke>
        <ns2:reply>
            <name>FC_UserLoginAck</name>
            <order>3</order>
        </ns2:reply>
        <ns2:invoke>
            <name>FC_FlexibusBookingRequest</name>
            <order>4</order>
        </ns2:invoke>
        <ns2:reply>
            <name>FC_TripDetails</name>
            <order>5</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>FC_ConfirmAndPay</name>
            <order>6</order>
        </ns2:reply>
        <ns2:invoke>
            <name>FC_PaymentReceipt</name>
            <order>7</order>
        </ns2:invoke>
        <ns2:reply>
            <name>FC_BookingResult</name>
            <order>8</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>FC_SetPassengerDetails</name>
            <order>9</order>
        </ns2:reply>
    </ns2:sequence>
</ns2:process>
