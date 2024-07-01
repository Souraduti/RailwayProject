<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" />

    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin="2cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-family="Times New Roman" font-size="30pt" font-weight="bold" text-align="center" margin-bottom="20pt" text-decoration="underline">
                        <xsl:value-of select="/document/title"/>
                    </fo:block>
                    <fo:block font-family="Times New Roman" margin-top="5" font-size="14" font-weight="bold">
                        <fo:inline font-weight="normal">Ticket Number: </fo:inline><xsl:value-of select="/document/ticket_number"/>
                    </fo:block>
                    <fo:block font-family="Times New Roman" margin-top="5" font-size="14" font-weight="bold">
                        <fo:inline font-weight="normal">Train Number: </fo:inline><xsl:value-of select="/document/train_number"/>
                    </fo:block>
                    <fo:block font-family="Times New Roman" margin-top="5" font-size="14" font-weight="bold">
                        <fo:inline font-weight="normal">Train Name: </fo:inline><xsl:value-of select="/document/train_name"/>
                    </fo:block>
                    <fo:block font-family="Times New Roman" font-size="12pt" margin-top="20">
                        <fo:table width="100%">
                            <fo:table-column column-width="20%"/>
                            <fo:table-column column-width="60%"/>
                            <fo:table-column column-width="20%"/>
                            <fo:table-body>
                                <!-- First row -->
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block font-family="Times New Roman" margin-top="2.5" margin-bottom="2.5" text-align="left" font-weight="bold">
                                            <fo:inline font-weight="normal">From : </fo:inline> <xsl:value-of select="/document/train_trip_details/from"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-family="Times New Roman" text-align="left">
                                            <fo:inline></fo:inline>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-family="Times New Roman" margin-top="2.5" margin-bottom="2.5" text-align="left" font-weight="bold">
                                            <fo:inline font-weight="normal">To : </fo:inline> <xsl:value-of select="/document/train_trip_details/to" />
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <!-- Second row -->
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block font-family="Times New Roman" margin-top="2.5" margin-bottom="2.5" text-align="left" font-weight="bold">
                                            <fo:inline font-weight="normal">Departure : </fo:inline> <xsl:value-of select="/document/train_trip_details/departure"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-family="Times New Roman" margin-top="2.5" margin-bottom="2.5" text-align="center" >
                                            <fo:inline font-weight="normal">----></fo:inline>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-family="Times New Roman" margin-top="2.5" margin-bottom="2.5" text-align="left" font-weight="bold">
                                            <fo:inline font-weight="normal">Arrival : </fo:inline> <xsl:value-of select="/document/train_trip_details/arrival"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:block font-family="Times New Roman" font-size="18" margin-top="15" margin-bottom="5">
                        <fo:inline text-decoration="underline" font-weight="bold">Passenger Details</fo:inline>
                    </fo:block>
                    <fo:block font-family="Times New Roman">
                        <fo:table border="0.5pt solid black" width="100%">
                            <fo:table-header>
                                <fo:table-row>
                                    <xsl:for-each select="/document/passenger_details/header/details">
                                        <fo:table-cell border="0.5pt solid black" padding="2pt">
                                            <fo:block font-family="Times New Roman" font-weight="bold" text-align="center">
                                                <xsl:value-of select="."/>
                                            </fo:block>
                                        </fo:table-cell>
                                    </xsl:for-each>
                                </fo:table-row>
                            </fo:table-header>
                            <fo:table-body>
                                <xsl:for-each select="/document/passenger_details/passenger">
                                    <fo:table-row>
                                        <xsl:for-each select="details">
                                            <fo:table-cell border="0.5pt solid black" padding="2pt">
                                                <fo:block font-family="Times New Roman" text-align="center">
                                                    <xsl:value-of select="."/>
                                                </fo:block>
                                            </fo:table-cell>
                                        </xsl:for-each>
                                    </fo:table-row>
                                </xsl:for-each>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
