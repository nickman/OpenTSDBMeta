/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.phoenix.udf;

import java.util.Iterator;
import java.util.List;

import org.apache.phoenix.compile.SequenceValueExpression;
import org.apache.phoenix.expression.AddExpression;
import org.apache.phoenix.expression.AndExpression;
import org.apache.phoenix.expression.ArrayConstructorExpression;
import org.apache.phoenix.expression.CaseExpression;
import org.apache.phoenix.expression.CoerceExpression;
import org.apache.phoenix.expression.ComparisonExpression;
import org.apache.phoenix.expression.DivideExpression;
import org.apache.phoenix.expression.Expression;
import org.apache.phoenix.expression.InListExpression;
import org.apache.phoenix.expression.IsNullExpression;
import org.apache.phoenix.expression.KeyValueColumnExpression;
import org.apache.phoenix.expression.LikeExpression;
import org.apache.phoenix.expression.LiteralExpression;
import org.apache.phoenix.expression.ModulusExpression;
import org.apache.phoenix.expression.MultiplyExpression;
import org.apache.phoenix.expression.NotExpression;
import org.apache.phoenix.expression.OrExpression;
import org.apache.phoenix.expression.ProjectedColumnExpression;
import org.apache.phoenix.expression.RowKeyColumnExpression;
import org.apache.phoenix.expression.RowValueConstructorExpression;
import org.apache.phoenix.expression.StringConcatExpression;
import org.apache.phoenix.expression.SubtractExpression;
import org.apache.phoenix.expression.function.ArrayAnyComparisonExpression;
import org.apache.phoenix.expression.function.ArrayElemRefExpression;
import org.apache.phoenix.expression.function.ScalarFunction;
import org.apache.phoenix.expression.function.SingleAggregateFunction;
import org.apache.phoenix.expression.visitor.ExpressionVisitor;

/**
 * <p>Title: EmptyExpressionVisitor</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.phoenix.udf.EmptyExpressionVisitor</code></p>
 */

public class EmptyExpressionVisitor implements ExpressionVisitor<Object> {

	/**
	 * Creates a new EmptyExpressionVisitor
	 */
	public EmptyExpressionVisitor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object defaultReturn(Expression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> defaultIterator(Expression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(AndExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(AndExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(OrExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(OrExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(ScalarFunction node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(ScalarFunction node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(ComparisonExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(ComparisonExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(LikeExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(LikeExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(SingleAggregateFunction node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(SingleAggregateFunction node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(CaseExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(CaseExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(NotExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(NotExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(InListExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(InListExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(IsNullExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(IsNullExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(SubtractExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(SubtractExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(MultiplyExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(MultiplyExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(AddExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(AddExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(DivideExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(DivideExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(CoerceExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(CoerceExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(ArrayConstructorExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(ArrayConstructorExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LiteralExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(RowKeyColumnExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(KeyValueColumnExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ProjectedColumnExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(SequenceValueExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(StringConcatExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(StringConcatExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(RowValueConstructorExpression node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(RowValueConstructorExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(ModulusExpression modulusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(ModulusExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(ArrayAnyComparisonExpression arrayAnyComparisonExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(ArrayAnyComparisonExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Expression> visitEnter(ArrayElemRefExpression arrayElemRefExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLeave(ArrayElemRefExpression node, List<Object> l) {
		// TODO Auto-generated method stub
		return null;
	}

}
