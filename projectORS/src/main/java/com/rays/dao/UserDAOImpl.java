package com.rays.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rays.common.BaseDAOImpl;
import com.rays.common.UserContext;
import com.rays.dto.RoleDTO;
import com.rays.dto.UserDTO;

/**
 * Contains User CRUD operations
 * 
 * @author Rupali patel
 *
 */
@Repository
public class UserDAOImpl extends BaseDAOImpl<UserDTO> implements UserDAOInt {

	@Override
	public Class<UserDTO> getDTOClass() {
		return UserDTO.class;
	}

	@Override
	protected List<Predicate> getWhereClause(UserDTO dto, CriteriaBuilder builder, Root<UserDTO> qRoot) {

		// Create where conditions
		List<Predicate> whereCondition = new ArrayList<Predicate>();

		if (!isEmptyString(dto.getFirstName())) {

			whereCondition.add(builder.like(qRoot.get("firstName"), dto.getFirstName() + "%"));
		}

		if (!isEmptyString(dto.getRoleName())) {

			whereCondition.add(builder.like(qRoot.get("roleName"), dto.getRoleName() + "%"));
		}
		if (!isEmptyString(dto.getLoginId())) {

			whereCondition.add(builder.equal(qRoot.get("loginId"), dto.getLoginId()));
		}
		if (!isEmptyString(dto.getPassword())) {

			whereCondition.add(builder.equal(qRoot.get("password"), dto.getPassword()));
		}

		if (!isEmptyString(dto.getStatus())) {

			whereCondition.add(builder.equal(qRoot.get("status"), dto.getStatus()));
		}

		if (!isZeroNumber(dto.getRoleId())) {

			whereCondition.add(builder.equal(qRoot.get("roleId"), dto.getRoleId()));
		}

		if (isNotNull(dto.getDob())) {

			whereCondition.add(builder.equal(qRoot.get("dob"), dto.getDob()));
		}

		return whereCondition;
	}

	@Autowired
	RoleDAOInt roleDao;

	@Override
	protected void populate(UserDTO dto, UserContext userContext) {
		if (dto.getRoleId() != null && dto.getRoleId() > 0) {
			RoleDTO roleDto = roleDao.findByPK(dto.getRoleId(), userContext);
			dto.setRoleName(roleDto.getName());
			System.out.println("Inside UserDaoImpl PopulateDto() Rolename" + dto.getRoleName());
		}
	}

	@Override
	public UserDTO findByEmail(String attribute, String val, UserContext userContext) {
		Class<UserDTO> dtoClass = getDTOClass();

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		System.out.println("Inside UserDaoImpl login = " + val);
		CriteriaQuery<UserDTO> cq = builder.createQuery(dtoClass);

		Root<UserDTO> qRoot = cq.from(dtoClass);

		Predicate condition = builder.equal(qRoot.get(attribute), val);

		if (userContext != null && !isZeroNumber(userContext.getOrgId())) {
			Predicate conditionGrp = builder.equal(qRoot.get("orgId"), userContext.getOrgId());
			cq.where(condition, conditionGrp);
		} else {
			cq.where(condition);
		}
		System.out.println("Inside UserDaoImpl Query = " + cq);
		TypedQuery<UserDTO> query = entityManager.createQuery(cq);

		List<UserDTO> list = query.getResultList();
		System.out.println("Inside UserDaoImpl list of user detail" + list.get(0));

		UserDTO dto = null;

		if (list.size() > 0) {
			dto = list.get(0);
			System.out.println("Inside UserDaoImpl login Id From database " + dto.getLoginId());
		}
		System.out.println("Inside UserDaoImpl return dto");
		return dto;

	}

}
