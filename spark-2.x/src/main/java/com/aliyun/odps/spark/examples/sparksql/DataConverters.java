/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aliyun.odps.spark.examples.sparksql;

import org.apache.spark.sql.types.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.sql.Row;

public class DataConverters {

    public interface RowDataToListConverter extends Serializable {
        void convert(Row in, List<Object> out);
    }

    public interface RowDataToJavaFieldConverter extends Serializable {
        Object convert(Row in, int index);
    }

    public static RowDataToListConverter createRecordConverter(StructField[] tableFields) {
        final RowDataToJavaFieldConverter[] fieldConverters =
                Arrays.stream(tableFields).map(field -> createConverter(field.dataType()))
                        .toArray(RowDataToJavaFieldConverter[]::new);
        return new RowDataToListConverter() {
            private static final long serialVersionUID = 1L;

            @Override
            public void convert(Row in, List<Object> out) {
                for (int i = 0; i < in.size(); ++i) {
                    Object valueObject =
                            fieldConverters[i].convert(in, i);
                    out.add(valueObject);
                }
            }
        };
    }

    public static RowDataToJavaFieldConverter createConverter(DataType dataType) {
        final RowDataToJavaFieldConverter converter;
        if (dataType instanceof BooleanType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getBoolean(index);
                }
            };
        } else if (dataType instanceof ByteType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getByte(index);
                }
            };
        } else if (dataType instanceof ShortType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getShort(index);
                }
            };
        } else if (dataType instanceof IntegerType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getInt(index);
                }
            };
        } else if (dataType instanceof LongType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getLong(index);
                }
            };
        } else if (dataType instanceof FloatType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getFloat(index);
                }
            };
        } else if (dataType instanceof DoubleType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getDouble(index);
                }
            };
        } else if (dataType instanceof DecimalType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getDecimal(index);
                }
            };
        } else if (dataType instanceof StringType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getString(index);
                }
            };
        } else if (dataType instanceof DateType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getDate(index);
                }
            };
        } else if (dataType instanceof TimestampType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getTimestamp(index);
                }
            };
        } else if (dataType instanceof BinaryType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return (byte[])in.get(index);
                }
            };
        } else if (dataType instanceof ArrayType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getList(index);
                }
            };
        } else if (dataType instanceof MapType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                public Object convert(Row in, int index) {
                    return in.getJavaMap(index);
                }
            };
        } else if (dataType instanceof StructType) {
            converter = new RowDataToJavaFieldConverter() {
                private static final long serialVersionUID = 1L;
                private final RowDataToListConverter converter =
                        createRecordConverter(((StructType)dataType).fields());
                public Object convert(Row in, int index) {
                    List<Object> out = new ArrayList<>();
                    converter.convert(in.getStruct(index), out);
                    return out;
                }
            };
        } else {
            throw new UnsupportedOperationException("Unsupported data type:" + dataType);
        }
        // wrap into nullable converter
        return new RowDataToJavaFieldConverter() {
            private static final long serialVersionUID = 1L;
            public Object convert(Row in, int index) {
                if (in == null) {
                    return null;
                }
                return converter.convert(in, index);
            }
        };
    }

}
