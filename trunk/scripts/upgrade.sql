ALTER TABLE app.telephones add constraint tel_unique unique(telephone);
ALTER TABLE app.telephones add constraint tel_10_numbers check(telephone BETWEEN 1000000000 AND 9999999999);