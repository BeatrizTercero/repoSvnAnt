<ant-lib version="1.0">

  <types>

    <XDtClass:forAllClasses type="org.apache.myrmidon.api.Task" extent="hierarchy">
      <XDtClass:ifHasClassTag tagName="ant.task">
        <task name="<XDtClass:classTagValue tagName="ant.task" paramName="name"/>"
              classname="<XDtClass:fullClassName/>"/>
      </XDtClass:ifHasClassTag>
    </XDtClass:forAllClasses>

    <XDtClass:forAllClasses type="org.apache.myrmidon.framework.DataType" extent="hierarchy">
      <XDtClass:ifHasClassTag tagName="ant.data-type">
        <data-type name="<XDtClass:classTagValue tagName="ant.data-type" paramName="name"/>"
              classname="<XDtClass:fullClassName/>"/>
        <task name="<XDtClass:classTagValue tagName="ant.data-type" paramName="name"/>"
              classname="org.apache.myrmidon.framework.TypeInstanceTask"/>
      </XDtClass:ifHasClassTag>
    </XDtClass:forAllClasses>

    <XDtClass:forAllClasses extent="concrete-type">
      <XDtClass:forAllClassTags tagName="ant.type" superclasses="false">
        <<XDtClass:classTagValue tagName="ant.type" paramName="type" superclasses="false"/>
          name="<XDtClass:classTagValue tagName="ant.type" paramName="name" superclasses="false"/>"
          classname="<XDtClass:fullClassName/>" />
      </XDtClass:forAllClassTags>
    </XDtClass:forAllClasses>

    <XDtClass:forAllClasses type="org.apache.aut.converter.Converter">
      <XDtClass:ifHasClassTag tagName="ant.converter">
        <converter classname="<XDtClass:fullClassName/>"
                   source="<XDtClass:classTagValue tagName="ant.converter" paramName="source"/>"
                   destination="<XDtClass:classTagValue tagName="ant.converter" paramName="destination"/>"/>
      </XDtClass:ifHasClassTag>
    </XDtClass:forAllClasses>

  </types>

</ant-lib>
