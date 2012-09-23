package org.grails.datastore.gorm.neo4j

import grails.plugin.spock.IntegrationSpec
import org.neo4j.graphdb.GraphDatabaseService
import grails.converters.JSON
import grails.web.JSONBuilder
import org.codehaus.groovy.grails.web.json.JSONElement
import neo4j.DummyDomain
import spock.lang.Ignore
import java.text.SimpleDateFormat
import spock.lang.IgnoreRest
import org.apache.commons.lang.CharRange
import org.neo4j.graphdb.DynamicRelationshipType

class GetOrSetSpec extends IntegrationSpec {

    GraphDatabaseService graphDatabaseService

    def "test getOrSet method on nodes"() {
        setup:
        def node = graphDatabaseService.createNode()

        when: 'setting a property'
        node."$propertyName" = propertyValue

        then: 'retrieving the property'
        node."$propertyName" == propertyValue

        where:
        propertyName  | propertyValue
        'name'        | 'abc'
        'createdDate' | new Date()
        'count'       | 5
        'price'       | 2.12f
    }

    def "marshalling test for nodes"() {
        when:
        def n = graphDatabaseService.createNode()
        n.setProperty('myproperty', 'myvalue')
        def json = marshalAsJSON(n)


        then:
        json.id == n.id
        json.myproperty == 'myvalue'
        json.relationships == []

    }

    def "marshalling test for relationship"() {
        when:
        def startNode = graphDatabaseService.createNode()
        startNode.setProperty('myproperty', 'startnode')
        def endNode = graphDatabaseService.createNode()
        endNode.setProperty('myproperty', 'endnode')
        def rel = startNode.createRelationshipTo(endNode, DynamicRelationshipType.withName('RELTYPE'))
        rel.setProperty('myproperty', 'rel')
        def json = marshalAsJSON(rel)

        then:
        json.id == rel.id
        json.myproperty == 'rel'
        json.startNode == startNode.id
        json.endNode == endNode.id
        json.type == 'RELTYPE'

    }

    private JSONElement marshalAsJSON(object) {
        def sw = new StringWriter()
        (object as JSON).render(sw)
        JSON.parse(sw.toString())
    }
}
