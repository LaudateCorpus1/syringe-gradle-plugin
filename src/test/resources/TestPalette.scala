package com.avast.test
import com.avast.syringe.config.perspective._
import javax.annotation.Generated
/**
 * Testing palette generation.
 *
 */
@Generated(value = Array("Syringe"))
trait TestPalette extends SyringeModule
with TestTrait
{

object TestClassBuilder
{
    type instanceType = TestClass
    val instanceClass = classOf[TestClass]
}
class TestClassBuilder (instanceClass: Class[_]) extends SyringeBuilder[TestClassBuilder.instanceType](instanceClass)
with TestBuilderTrait[TestClassBuilder.instanceType]
{
    def this() = this(TestClassBuilder.instanceClass)
    def set(propertyName: String, value: => Any): this.type = inject(propertyName, value)
}

def newTestClass = new TestClassBuilder().initialize
lazy val testClass = __sm__[TestClassBuilder](newTestClass)

}
