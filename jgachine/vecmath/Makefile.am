JAVAROOT = $(CURDIR)

include $(top_srcdir)/Common.mk

sources = \
	javax/vecmath/AxisAngle4d.java \
	javax/vecmath/AxisAngle4f.java \
	javax/vecmath/GMatrix.java \
	javax/vecmath/GVector.java \
	javax/vecmath/Matrix3d.java \
	javax/vecmath/Matrix3f.java \
	javax/vecmath/Matrix4d.java \
	javax/vecmath/Matrix4f.java \
	javax/vecmath/MismatchedSizeException.java \
	javax/vecmath/Point2d.java \
	javax/vecmath/Point2f.java \
	javax/vecmath/Point3d.java \
	javax/vecmath/Point3f.java \
	javax/vecmath/Point3i.java \
	javax/vecmath/Point4d.java \
	javax/vecmath/Point4f.java \
	javax/vecmath/Point4i.java \
	javax/vecmath/Quat4d.java \
	javax/vecmath/Quat4f.java \
	javax/vecmath/SingularMatrixException.java \
	javax/vecmath/TexCoord2f.java \
	javax/vecmath/TexCoord3f.java \
	javax/vecmath/Tuple2d.java \
	javax/vecmath/Tuple2f.java \
	javax/vecmath/Tuple3b.java \
	javax/vecmath/Tuple3d.java \
	javax/vecmath/Tuple3f.java \
	javax/vecmath/Tuple3i.java \
	javax/vecmath/Tuple4b.java \
	javax/vecmath/Tuple4d.java \
	javax/vecmath/Tuple4f.java \
	javax/vecmath/Tuple4i.java \
	javax/vecmath/VecmathTest.java \
	javax/vecmath/Vector2d.java \
	javax/vecmath/Vector2f.java \
	javax/vecmath/Vector3d.java \
	javax/vecmath/Vector3f.java \
	javax/vecmath/Vector4d.java \
	javax/vecmath/Vector4f.java

#class_JAVA = $(sources)

classes = $(subst /,.,$(sources:.java=))
created_class = $(sources:.java=.class)
created_h = $(sources:.java=.h)

libvecmath_a_SOURCES = $(sources)
noinst_LIBRARIES = libvecmath.a


vecmath.jar: $(created_class)
	$(JAR) cvf $@ $(created_class)

jar_DATA = vecmath.jar


all-local: $(created_class) $(created_h)

MOSTLYCLEANFILES = vecmath.jar $(created_class) $(created_h)
