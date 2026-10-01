DROP TABLE TBL_CUSTOMER CASCADE CONSTRAINTS;
DROP TABLE TBL_EMPLOYEE CASCADE CONSTRAINTS;
DROP TABLE TBL_SALE CASCADE CONSTRAINTS;
DROP TABLE TBL_SALESGOAL CASCADE CONSTRAINTS;
DROP TABLE TBL_VEHICLE CASCADE CONSTRAINTS;

/*==============================================================*/
/* Table: TBL_CUSTOMER                                          */
/*==============================================================*/
CREATE TABLE TBL_CUSTOMER 
(
   CUS_ID               NUMBER               NOT NULL,
   CUS_NAME             VARCHAR2(50)         NOT NULL,
   CUS_PHONE            VARCHAR2(10)         NOT NULL,
   CUS_EMAIL            VARCHAR2 (50),
   CUS_STATE            VARCHAR2(10),
   CONSTRAINT PK_TBL_CUSTOMER PRIMARY KEY (CUS_ID)
);

/*==============================================================*/
/* Table: TBL_EMPLOYEE                                          */
/*==============================================================*/
CREATE TABLE TBL_EMPLOYEE 
(
   EMP_ID               NUMBER               NOT NULL,
   EMP_NAME             VARCHAR2(50)         NOT NULL,
   EMP_PHONE            VARCHAR2(10)         NOT NULL,
   EMP_SALARY           NUMBER(8)            NOT NULL,
   EMP_HIRE_DATE        DATE,
   EMP_ROLE             VARCHAR2(7)          NOT NULL
      CONSTRAINT CKC_EMP_ROLE_TBL_EMPL CHECK (EMP_ROLE IN ('manager','seller')),
   EMP_STATE            VARCHAR2(10),
   CONSTRAINT PK_TBL_EMPLOYEE PRIMARY KEY (EMP_ID)
);

/*==============================================================*/
/* Table: TBL_VEHICLE (Unificada con atributos de TBL_UNIT)     */
/*==============================================================*/
CREATE TABLE TBL_VEHICLE 
(
   VEH_ID               NUMBER               NOT NULL,
   VEH_MODEL            VARCHAR2 (25)        NOT NULL,
   VEH_YEAR             NUMBER,
   VEH_BODY_TYPE        VARCHAR2(25)         NOT NULL,
   VEH_FUEL_TYPE        VARCHAR2(8)          NOT NULL
      CONSTRAINT CKC_VEH_FUEL_TYPE_TBL_VEHI CHECK (VEH_FUEL_TYPE IN ('electric','gasoline','hybrid')),
   VEH_CATEGORY         VARCHAR2(8)          NOT NULL
      CONSTRAINT CKC_VEH_CATEGORY_TBL_VEHI CHECK (VEH_CATEGORY IN ('standard','luxury')),
   VEH_LICENSE_PLATE    VARCHAR2(6)          NOT NULL,
   VEH_MILEAGE          NUMBER,
   VEH_CONDITION        VARCHAR2(4)          NOT NULL
      CONSTRAINT CKC_VEH_CONDITION_TBL_VEHI CHECK (VEH_CONDITION IN ('used','new')),
   VEH_STATUS           VARCHAR2(9)          NOT NULL
      CONSTRAINT CKC_VEH_STATUS_TBL_VEHI CHECK (VEH_STATUS IN ('available','reserved','sold')),
   CONSTRAINT UK_VEH_LICENSE_PLATE UNIQUE (VEH_LICENSE_PLATE),
   CONSTRAINT PK_TBL_VEHICLE PRIMARY KEY (VEH_ID)
);

/*==============================================================*/
/* Table: TBL_SALE                                              */
/*==============================================================*/
CREATE TABLE TBL_SALE 
(
   SALE_ID              NUMBER               NOT NULL,
   CUS_ID               NUMBER               NOT NULL,
   EMP_ID               NUMBER               NOT NULL,
   VEH_ID               NUMBER               NOT NULL,
   SALE_DATE_START      DATE                 NOT NULL,
   SALE_PRICE           NUMBER(15,2)         NOT NULL,
   SALE_STATUS          VARCHAR2(10)         NOT NULL
      CONSTRAINT CKC_SALE_STATUS_TBL_SALE CHECK (SALE_STATUS IN ('confirmed','cancelled','inprogress')),
   SALE_DATE_END        DATE,
   CONSTRAINT PK_TBL_SALE PRIMARY KEY (SALE_ID)
);

CREATE INDEX IS_MADE_BY_FK ON TBL_SALE (CUS_ID ASC);
CREATE INDEX PROCESSES_FK ON TBL_SALE (EMP_ID ASC);
CREATE INDEX IS_SOLD_IN_FK ON TBL_SALE (VEH_ID ASC);

/*==============================================================*/
/* Table: TBL_SALESGOAL                                         */
/*==============================================================*/
CREATE TABLE TBL_SALESGOAL 
(
   SGL_ID               NUMBER               NOT NULL,
   EMP_ID               NUMBER,
   SGL_GOAL_TYPE        VARCHAR2(9)         
      CONSTRAINT CKC_SGL_GOAL_TYPE_TBL_SALE CHECK (SGL_GOAL_TYPE IS NULL OR (SGL_GOAL_TYPE IN ('monthly','quarterly','yearly'))),
   SGL_TARGET_VALUE     NUMBER(15,3)         NOT NULL,
   SGL_START_DATE       DATE                 NOT NULL,
   SGL_END_DATE         DATE                 NOT NULL,
   SGL_STATE            VARCHAR2(10)         NOT NULL,
   CONSTRAINT PK_TBL_SALESGOAL PRIMARY KEY (SGL_ID)
);

CREATE INDEX ES_ASIGNADA_FK ON TBL_SALESGOAL (EMP_ID ASC);

/*==============================================================*/
/* Constraints Relacionales y de Unicidad                       */
/*==============================================================*/
ALTER TABLE TBL_SALE
   ADD CONSTRAINT FK_TBL_SALE_IS_MADE_B_TBL_CUST FOREIGN KEY (CUS_ID)
      REFERENCES TBL_CUSTOMER (CUS_ID);

ALTER TABLE TBL_SALE
   ADD CONSTRAINT FK_TBL_SALE_IS_SOLD_I_TBL_VEHI FOREIGN KEY (VEH_ID)
      REFERENCES TBL_VEHICLE (VEH_ID);

ALTER TABLE TBL_SALE
   ADD CONSTRAINT FK_TBL_SALE_PROCESSES_TBL_EMPL FOREIGN KEY (EMP_ID)
      REFERENCES TBL_EMPLOYEE (EMP_ID);

ALTER TABLE TBL_SALESGOAL
   ADD CONSTRAINT FK_TBL_SALE_ES_ASIGNA_TBL_EMPL FOREIGN KEY (EMP_ID)
      REFERENCES TBL_EMPLOYEE (EMP_ID);

/* Garantiza un único gerente general en la empresa */
CREATE UNIQUE INDEX ux_one_manager ON TBL_EMPLOYEE(
   CASE
      WHEN UPPER(emp_role) = 'MANAGER' THEN 1
      ELSE NULL
   END
);

ALTER TABLE TBL_EMPLOYEE ADD CONSTRAINT UK_EMP_PHONE UNIQUE (EMP_PHONE);
ALTER TABLE TBL_CUSTOMER ADD CONSTRAINT UK_CUS_PHONE UNIQUE (CUS_PHONE);
ALTER TABLE TBL_CUSTOMER ADD CONSTRAINT UK_CUS_EMAIL UNIQUE (CUS_EMAIL);