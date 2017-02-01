<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:process xmlns:ns2="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    <ns2:sequence>
        <ns2:invoke>
            <name>PR_RegistrationRequest</name>
            <order>0</order>
        </ns2:invoke>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_concrete_1</name>
            <order>1</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_concrete_2</name>
            <order>2</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_concrete_3</name>
            <order>3</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_concrete_4</name>
            <order>4</order>
        </ns2:reply>
        <ns2:reply>
            <name>PR_RegistrationAck</name>
            <order>5</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_bookPark</name>
            <order>6</order>
        </ns2:reply>
        <ns2:invoke>
            <name>PR_FindPArkRequest</name>
            <order>7</order>
        </ns2:invoke>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_concrete1</name>
            <order>8</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_concrete2</name>
            <order>9</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_concrete3</name>
            <order>10</order>
        </ns2:reply>
        <ns2:reply xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="processActivity">
            <name>PR_PlanCarRoute</name>
            <order>11</order>
        </ns2:reply>
        <ns2:reply>
            <name>PR_FindPArkAck</name>
            <order>12</order>
        </ns2:reply>
    </ns2:sequence>
</ns2:process>
